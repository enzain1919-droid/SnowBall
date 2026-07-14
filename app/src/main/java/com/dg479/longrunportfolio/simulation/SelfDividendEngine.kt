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
    val withdrawalGrowthRate: Double
)

data class SelfDividendProjectionRow(
    val year: Int,
    val monthlyTakeHome: Long,
    val annualTakeHome: Long,
    val assetBeforeWithdrawal: Long,
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
            .filter { it.investmentAmount > 0.0 && it.baseAnnualWithdrawal > 0.0 }
            .map { asset ->
                LotState(
                    taxable = asset.taxable,
                    expectedAnnualReturn = asset.expectedAnnualReturn.coerceAtLeast(-0.99),
                    baseAnnualWithdrawal = asset.baseAnnualWithdrawal,
                    withdrawalGrowthRate = asset.withdrawalGrowthRate,
                    assetValue = asset.investmentAmount,
                    remainingCostBasis = asset.investmentAmount
                )
            }
        if (lots.isEmpty() || years <= 0) return emptyList()

        return (0 until years).map { index ->
            val year = index + 1
            lots.forEach { lot ->
                lot.assetValue *= 1.0 + lot.expectedAnnualReturn
            }

            val assetBeforeWithdrawal = lots.sumOf { it.assetValue }
            val annualWithdrawal = lots.sumOf { lot ->
                lot.baseAnnualWithdrawal * (1.0 + lot.withdrawalGrowthRate).pow(index)
            }
            val sale = estimateSale(lots, annualWithdrawal)
            applySale(lots, sale.grossSale)

            SelfDividendProjectionRow(
                year = year,
                monthlyTakeHome = (sale.netProceeds / 12.0).roundToLong(),
                annualTakeHome = sale.netProceeds.roundToLong(),
                assetBeforeWithdrawal = assetBeforeWithdrawal.roundToLong(),
                grossSale = sale.grossSale.roundToLong(),
                realizedGain = sale.realizedGain.roundToLong(),
                capitalGainsTax = sale.capitalGainsTax.roundToLong(),
                totalAsset = lots.sumOf { it.assetValue }.roundToLong(),
                remainingCostBasis = lots.sumOf { it.remainingCostBasis }.roundToLong(),
                note = resultNote(sale, annualWithdrawal)
            )
        }
    }

    private fun estimateSale(lots: List<LotState>, targetNetWithdrawal: Double): SaleEstimate {
        val totalAsset = lots.sumOf { it.assetValue }.coerceAtLeast(0.0)
        if (targetNetWithdrawal <= 0.0 || totalAsset <= 0.0) {
            return SaleEstimate()
        }

        val fullSaleTax = calculateTax(lots, totalAsset)
        val fullSaleNet = totalAsset - fullSaleTax.capitalGainsTax
        if (fullSaleNet <= targetNetWithdrawal) {
            return SaleEstimate(
                grossSale = totalAsset,
                netProceeds = fullSaleNet.coerceAtLeast(0.0),
                realizedGain = fullSaleTax.realizedGain,
                capitalGainsTax = fullSaleTax.capitalGainsTax
            )
        }

        var low = 0.0
        var high = totalAsset
        repeat(80) {
            val mid = (low + high) / 2.0
            val tax = calculateTax(lots, mid)
            if (mid - tax.capitalGainsTax >= targetNetWithdrawal) high = mid else low = mid
        }

        val tax = calculateTax(lots, high)
        return SaleEstimate(
            grossSale = high,
            netProceeds = (high - tax.capitalGainsTax).coerceAtLeast(0.0),
            realizedGain = tax.realizedGain,
            capitalGainsTax = tax.capitalGainsTax
        )
    }

    private fun calculateTax(lots: List<LotState>, saleAmount: Double): SaleEstimate {
        val totalAsset = lots.sumOf { it.assetValue }
        if (saleAmount <= 0.0 || totalAsset <= 0.0) return SaleEstimate()

        var realizedGain = 0.0
        lots.forEach { lot ->
            if (!lot.taxable || lot.assetValue <= 0.0) return@forEach
            val saleFromLot = saleAmount * lot.assetValue / totalAsset
            val unrealizedGain = lot.assetValue - lot.remainingCostBasis
            realizedGain += saleFromLot * unrealizedGain / lot.assetValue
        }

        val tax = (realizedGain - AnnualBasicDeductionWon)
            .coerceAtLeast(0.0) * CapitalGainsTaxRate
        return SaleEstimate(
            grossSale = saleAmount,
            netProceeds = saleAmount - tax,
            realizedGain = realizedGain,
            capitalGainsTax = tax
        )
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
        sale.grossSale <= 0.0 -> "인출 없음"
        sale.netProceeds < targetWithdrawal * 0.999 -> "자산 소진으로 목표 인출액 일부만 지급"
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
        var assetValue: Double,
        var remainingCostBasis: Double
    )

    private data class SaleEstimate(
        val grossSale: Double = 0.0,
        val netProceeds: Double = 0.0,
        val realizedGain: Double = 0.0,
        val capitalGainsTax: Double = 0.0
    )
}
