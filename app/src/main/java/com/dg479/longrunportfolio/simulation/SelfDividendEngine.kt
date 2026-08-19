package com.dg479.longrunportfolio.simulation

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToLong

data class SelfDividendAssetInput(
    val taxable: Boolean,
    val expectedAnnualReturn: Double,
    val investmentAmount: Double,
    val baseAnnualWithdrawal: Double,
    val withdrawalGrowthRate: Double,
    val annualDividendYield: Double = 0.0,
    val dividendWithholdingTaxRate: Double = 0.0
)

data class SelfDividendProjectionRow(
    val year: Int,
    val monthlyTakeHome: Long,
    val annualTakeHome: Long,
    val assetBeforeWithdrawal: Long,
    val grossDividend: Long,
    val dividendTax: Long,
    val afterTaxDividend: Long,
    val grossSale: Long,
    val realizedGain: Long,
    val capitalGainsTax: Long,
    val totalAsset: Long,
    val remainingCostBasis: Long,
    val note: String
)

object SelfDividendEngine {
    private const val AnnualBasicDeductionWon = 2_500_000.0
    private const val CapitalGainsTaxRate = 0.22

    fun calculate(
        assets: List<SelfDividendAssetInput>,
        years: Int = 20
    ): List<SelfDividendProjectionRow> {
        val lots = assets
            .filter { it.investmentAmount > 0.0 && it.baseAnnualWithdrawal >= 0.0 }
            .map { asset ->
                LotState(
                    taxable = asset.taxable,
                    expectedAnnualReturn = asset.expectedAnnualReturn.coerceAtLeast(-0.99),
                    baseAnnualWithdrawal = asset.baseAnnualWithdrawal,
                    withdrawalGrowthRate = asset.withdrawalGrowthRate,
                    annualDividendYield = asset.annualDividendYield.coerceAtLeast(0.0),
                    dividendWithholdingTaxRate = asset.dividendWithholdingTaxRate.coerceIn(0.0, 1.0),
                    assetValue = asset.investmentAmount,
                    remainingCostBasis = asset.investmentAmount
                )
            }
        if (lots.isEmpty() || years < 0) return emptyList()

        return (0..years).map { year ->
            val assetBeforeWithdrawal = lots.sumOf { it.assetValue }
            val annualWithdrawal = lots.sumOf { lot ->
                lot.baseAnnualWithdrawal * (1.0 + lot.withdrawalGrowthRate).pow(year)
            }
            val monthlyWithdrawal = annualWithdrawal / 12.0
            val annualDividends = lots.map { lot ->
                lot.assetValue * lot.annualDividendYield
            }
            val grossDividend = annualDividends.sum()
            val withholdingTax = lots.indices.sumOf { index ->
                annualDividends[index] * lots[index].dividendWithholdingTaxRate
            }
            val effectiveWithholdingTaxRate = if (grossDividend > 0.0) {
                withholdingTax / grossDividend
            } else {
                0.0
            }
            val afterTaxDividend = DividendTaxEngine.afterTaxAnnualWon(
                grossAnnualDividendWon = grossDividend,
                person = DividendTaxPerson(),
                withholdingTaxRate = effectiveWithholdingTaxRate
            )
            val dividendTax = (grossDividend - afterTaxDividend).coerceAtLeast(0.0)
            val monthlyAfterTaxDividend = afterTaxDividend / 12.0
            var grossSale = 0.0
            var realizedGain = 0.0

            repeat(12) {
                lots.forEach { lot ->
                    val monthlyReturn = (1.0 + lot.expectedAnnualReturn).pow(1.0 / 12.0) - 1.0
                    lot.assetValue *= 1.0 + monthlyReturn
                }

                val saleAmount = (monthlyWithdrawal - monthlyAfterTaxDividend)
                    .coerceAtLeast(0.0)
                    .coerceAtMost(lots.sumOf { it.assetValue }.coerceAtLeast(0.0))
                realizedGain += calculateRealizedGain(lots, saleAmount)
                applySale(lots, saleAmount)
                grossSale += saleAmount
            }

            val capitalGainsTax = (realizedGain - AnnualBasicDeductionWon)
                .coerceAtLeast(0.0) * CapitalGainsTaxRate
            val annualTakeHome = (grossSale - capitalGainsTax + afterTaxDividend).coerceAtLeast(0.0)
            val sale = SaleEstimate(
                grossSale = grossSale,
                afterTaxDividend = afterTaxDividend,
                realizedGain = realizedGain,
                capitalGainsTax = capitalGainsTax
            )

            SelfDividendProjectionRow(
                year = year,
                monthlyTakeHome = (annualTakeHome / 12.0).roundToLong(),
                annualTakeHome = annualTakeHome.roundToLong(),
                assetBeforeWithdrawal = assetBeforeWithdrawal.roundToLong(),
                grossDividend = grossDividend.roundToLong(),
                dividendTax = dividendTax.roundToLong(),
                afterTaxDividend = afterTaxDividend.roundToLong(),
                grossSale = grossSale.roundToLong(),
                realizedGain = realizedGain.roundToLong(),
                capitalGainsTax = capitalGainsTax.roundToLong(),
                totalAsset = lots.sumOf { it.assetValue }.roundToLong(),
                remainingCostBasis = lots.sumOf { it.remainingCostBasis }.roundToLong(),
                note = resultNote(sale, annualWithdrawal)
            )
        }
    }

    private fun calculateRealizedGain(lots: List<LotState>, saleAmount: Double): Double {
        val totalAsset = lots.sumOf { it.assetValue }
        if (saleAmount <= 0.0 || totalAsset <= 0.0) return 0.0

        var realizedGain = 0.0
        lots.forEach { lot ->
            if (!lot.taxable || lot.assetValue <= 0.0) return@forEach
            val saleFromLot = saleAmount * lot.assetValue / totalAsset
            val unrealizedGain = lot.assetValue - lot.remainingCostBasis
            realizedGain += saleFromLot * unrealizedGain / lot.assetValue
        }
        return realizedGain
    }

    private fun applySale(lots: List<LotState>, saleAmount: Double) {
        val totalAsset = lots.sumOf { it.assetValue }
        if (saleAmount <= 0.0 || totalAsset <= 0.0) return

        val remainingRatio = 1.0 - (saleAmount / totalAsset).coerceIn(0.0, 1.0)
        lots.forEach { lot ->
            lot.assetValue = (lot.assetValue * remainingRatio).coerceAtLeast(0.0)
            lot.remainingCostBasis = (lot.remainingCostBasis * remainingRatio).coerceAtLeast(0.0)
        }
    }

    private fun resultNote(sale: SaleEstimate, targetWithdrawal: Double): String = when {
        sale.grossSale + sale.afterTaxDividend <= 0.0 -> "인출 없음"
        sale.grossSale + sale.afterTaxDividend < targetWithdrawal * 0.999 ->
            "자산 소진으로 목표 인출액 일부만 지급"
        sale.capitalGainsTax > 0.0 ->
            "실현차익 ${formatWon(sale.realizedGain.roundToLong())}에서 기본공제 250만원 적용"
        sale.realizedGain > 0.0 ->
            "실현차익 ${formatWon(sale.realizedGain.roundToLong())}이 기본공제 250만원 이하"
        else -> "실현손익 ${formatWon(sale.realizedGain.roundToLong())}로 양도세 없음"
    }

    private fun formatWon(value: Long): String =
        "${NumberFormat.getNumberInstance(Locale.KOREA).format(value)}원"

    private data class LotState(
        val taxable: Boolean,
        val expectedAnnualReturn: Double,
        val baseAnnualWithdrawal: Double,
        val withdrawalGrowthRate: Double,
        val annualDividendYield: Double,
        val dividendWithholdingTaxRate: Double,
        var assetValue: Double,
        var remainingCostBasis: Double
    )

    private data class SaleEstimate(
        val grossSale: Double = 0.0,
        val afterTaxDividend: Double = 0.0,
        val realizedGain: Double = 0.0,
        val capitalGainsTax: Double = 0.0
    )
}
