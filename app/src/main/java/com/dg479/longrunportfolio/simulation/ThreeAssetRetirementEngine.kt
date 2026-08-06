package com.dg479.longrunportfolio.simulation

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

data class ThreeAssetAllocation(
    val schd: Double,
    val jepq: Double,
    val gpiq: Double,
    val voo: Double,
    val qld: Double,
    val cash: Double = 0.0
)

data class ThreeAssetPositionInput(
    val ticker: String,
    val name: String,
    val allocation: Double,
    val price: Double,
    val dividendYield: Double,
    val dividendGrowth: Double,
    val priceGrowth: Double,
    val stressPriceMultiplier: Double = 0.7,
    val stressDividendMultiplier: Double = 1.0
)

data class ThreeAssetRetirementInput(
    val totalCapitalWon: Long,
    val monthlyExpenseWon: Long,
    val allocation: ThreeAssetAllocation,
    val exchangeRate: Double,
    val schdPrice: Double,
    val jepqPrice: Double,
    val gpiqPrice: Double,
    val vooPrice: Double,
    val qldPrice: Double,
    val schdYield: Double,
    val schdDividendGrowth: Double,
    val schdPriceGrowth: Double,
    val jepqYield: Double,
    val jepqDividendGrowth: Double,
    val jepqPriceGrowth: Double,
    val gpiqYield: Double,
    val gpiqDividendGrowth: Double,
    val gpiqPriceGrowth: Double,
    val vooPriceGrowth: Double,
    val qldPriceGrowth: Double,
    val cashYield: Double,
    val inflationRate: Double,
    val overseasDividendTaxRate: Double,
    val taxAndInsuranceRate: Double,
    val stressTestEnabled: Boolean,
    val assets: List<ThreeAssetPositionInput> = emptyList()
)

data class ThreeAssetAnnualRow(
    val year: Int,
    val schdAssetWon: Long,
    val jepqAssetWon: Long,
    val gpiqAssetWon: Long,
    val vooAssetWon: Long,
    val qldAssetWon: Long,
    val cashWon: Long,
    val grossAnnualDividendWon: Long,
    val netAnnualDividendWon: Long,
    val annualExpenseWon: Long,
    val actualAnnualCashFlowWon: Long,
    val action: String,
    val totalAssetWon: Long,
    val assetValuesWon: Map<String, Long> = emptyMap()
)

data class ThreeAssetRetirementResult(
    val initialSchdShares: Double,
    val initialJepqShares: Double,
    val initialGpiqShares: Double,
    val initialVooShares: Double,
    val initialQldShares: Double,
    val cashDepletedYear: Int?,
    val finalAssetWon: Long,
    val inflationTargetWon: Long,
    val realGrowthPercent: Double,
    val rows: List<ThreeAssetAnnualRow>,
    val initialSharesByTicker: Map<String, Double> = emptyMap()
)

object ThreeAssetRetirementEngine {
    fun calculate(input: ThreeAssetRetirementInput, years: Int = 20): ThreeAssetRetirementResult {
        val assetInputs = input.assets.takeIf { it.isNotEmpty() } ?: legacyAssets(input)

        data class PositionState(
            val input: ThreeAssetPositionInput,
            val shares: Double,
            var multiplier: Double = 1.0,
            var currentPrice: Double = input.price
        )

        val positions = assetInputs.map { rawAsset ->
            val safePrice = rawAsset.price.coerceAtLeast(0.000001)
            val asset = rawAsset.copy(
                ticker = rawAsset.ticker.trim().uppercase(Locale.US),
                allocation = rawAsset.allocation.coerceAtLeast(0.0),
                price = safePrice
            )
            PositionState(
                input = asset,
                shares = input.totalCapitalWon * asset.allocation / input.exchangeRate.coerceAtLeast(0.000001) / safePrice
            )
        }
        val initialSharesByTicker = positions.associate { it.input.ticker to it.shares }
        var cashValue = input.totalCapitalWon * input.allocation.cash
        var annualExpense = input.monthlyExpenseWon * 12.0
        var cashDepletedYear: Int? = null
        var finalYearTotal = 0.0
        val rows = mutableListOf<ThreeAssetAnnualRow>()

        for (year in 1..years.coerceAtLeast(0)) {
            positions.forEach { position ->
                if (input.stressTestEnabled && year == 1) {
                    position.currentPrice = position.input.price * position.input.stressPriceMultiplier.coerceIn(0.0, 1.0)
                } else if (!input.stressTestEnabled || year > 3) {
                    position.currentPrice *= 1.0 + position.input.priceGrowth
                }
            }

            val assetValues = positions.associate { position ->
                position.input.ticker to
                    position.shares * position.multiplier * position.currentPrice * input.exchangeRate
            }.toMutableMap()
            var availableCash = cashValue

            fun dividendFactor(position: PositionState): Double {
                if (!input.stressTestEnabled) {
                    return (1.0 + position.input.dividendGrowth).pow((year - 1).toDouble())
                }
                if (year <= 3) return position.input.stressDividendMultiplier.coerceIn(0.0, 1.0)
                return (1.0 + position.input.dividendGrowth).pow((year - 3).toDouble())
            }

            val grossDividend = positions.sumOf { position ->
                position.shares * position.multiplier * position.input.price * position.input.dividendYield *
                    dividendFactor(position) * input.exchangeRate
            } + availableCash.coerceAtLeast(0.0) * input.cashYield
            val totalDividendDeductionRate = (
                input.overseasDividendTaxRate.coerceAtLeast(0.0) +
                    input.taxAndInsuranceRate.coerceAtLeast(0.0)
                ).coerceAtMost(1.0)
            val netDividend = grossDividend * (1.0 - totalDividendDeductionRate)
            val actualAnnualCashFlow = minOf(
                annualExpense.coerceAtLeast(0.0),
                (netDividend + availableCash + assetValues.values.sum()).coerceAtLeast(0.0)
            )
            val shortfall = annualExpense - netDividend
            val actions = mutableListOf<String>()

            if (shortfall > 0.0) {
                var remainingShortfall = shortfall
                val cashDrawn = minOf(availableCash, remainingShortfall)
                if (cashDrawn > 0.0) {
                    availableCash -= cashDrawn
                    remainingShortfall -= cashDrawn
                    actions += "현금 인출: ${formatManWon(cashDrawn)}"
                }
                if (remainingShortfall > 0.0 && cashDepletedYear == null) cashDepletedYear = year

                positions.forEach { position ->
                    val availableValue = assetValues[position.input.ticker] ?: 0.0
                    if (remainingShortfall <= 0.0 || availableValue <= 0.0) return@forEach
                    val sale = minOf(availableValue, remainingShortfall)
                    position.multiplier *= 1.0 - sale / availableValue
                    assetValues[position.input.ticker] = availableValue - sale
                    remainingShortfall -= sale
                    actions += "${position.input.ticker} 매도: ${formatManWon(sale)}"
                }
                if (remainingShortfall > 0.0) actions += "계좌 소진"
            } else {
                val surplus = abs(shortfall)
                availableCash += surplus
                actions += "현금 적립: ${formatManWon(surplus)}"
            }

            cashValue = availableCash
            val valueByTicker = assetValues.mapValues { (_, value) -> value.roundToLong().coerceAtLeast(0L) }
            val endTotal = assetValues.values.sum() + cashValue
            finalYearTotal = endTotal
            rows += ThreeAssetAnnualRow(
                year = year,
                schdAssetWon = valueByTicker["SCHD"] ?: 0L,
                jepqAssetWon = valueByTicker["JEPQ"] ?: 0L,
                gpiqAssetWon = valueByTicker["GPIQ"] ?: 0L,
                vooAssetWon = valueByTicker["VOO"] ?: 0L,
                qldAssetWon = valueByTicker["QLD"] ?: 0L,
                cashWon = cashValue.roundToLong().coerceAtLeast(0L),
                grossAnnualDividendWon = grossDividend.roundToLong().coerceAtLeast(0L),
                netAnnualDividendWon = netDividend.roundToLong().coerceAtLeast(0L),
                annualExpenseWon = annualExpense.roundToLong().coerceAtLeast(0L),
                actualAnnualCashFlowWon = actualAnnualCashFlow.roundToLong().coerceAtLeast(0L),
                action = actions.joinToString(" / "),
                totalAssetWon = endTotal.roundToLong().coerceAtLeast(0L),
                assetValuesWon = valueByTicker
            )
            annualExpense *= 1.0 + input.inflationRate
        }

        val inflationTarget = input.totalCapitalWon * (1.0 + input.inflationRate).pow(years.coerceAtLeast(0).toDouble())
        val realGrowthPercent = if (inflationTarget > 0.0) {
            (finalYearTotal - inflationTarget) / inflationTarget * 100.0
        } else {
            0.0
        }
        return ThreeAssetRetirementResult(
            initialSchdShares = initialSharesByTicker["SCHD"] ?: 0.0,
            initialJepqShares = initialSharesByTicker["JEPQ"] ?: 0.0,
            initialGpiqShares = initialSharesByTicker["GPIQ"] ?: 0.0,
            initialVooShares = initialSharesByTicker["VOO"] ?: 0.0,
            initialQldShares = initialSharesByTicker["QLD"] ?: 0.0,
            cashDepletedYear = cashDepletedYear,
            finalAssetWon = finalYearTotal.roundToLong().coerceAtLeast(0L),
            inflationTargetWon = inflationTarget.roundToLong().coerceAtLeast(0L),
            realGrowthPercent = realGrowthPercent,
            rows = rows,
            initialSharesByTicker = initialSharesByTicker
        )
    }

    private fun legacyAssets(input: ThreeAssetRetirementInput): List<ThreeAssetPositionInput> = listOf(
        ThreeAssetPositionInput("SCHD", "SCHD", input.allocation.schd, input.schdPrice, input.schdYield, input.schdDividendGrowth, input.schdPriceGrowth),
        ThreeAssetPositionInput("JEPQ", "JEPQ", input.allocation.jepq, input.jepqPrice, input.jepqYield, input.jepqDividendGrowth, input.jepqPriceGrowth, stressDividendMultiplier = 0.8),
        ThreeAssetPositionInput("GPIQ", "GPIQ", input.allocation.gpiq, input.gpiqPrice, input.gpiqYield, input.gpiqDividendGrowth, input.gpiqPriceGrowth, stressDividendMultiplier = 0.8),
        ThreeAssetPositionInput("VOO", "VOO", input.allocation.voo, input.vooPrice, 0.0, 0.0, input.vooPriceGrowth),
        ThreeAssetPositionInput("QLD", "QLD", input.allocation.qld, input.qldPrice, 0.0, 0.0, input.qldPriceGrowth, stressPriceMultiplier = 0.4)
    )

    private fun formatManWon(value: Double): String =
        "${NumberFormat.getNumberInstance(Locale.KOREA).format((value / 10_000.0).roundToLong())}만"
}
