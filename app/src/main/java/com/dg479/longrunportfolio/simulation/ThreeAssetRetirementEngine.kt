package com.dg479.longrunportfolio.simulation

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

data class ThreeAssetAllocation(
    val schd: Double,
    val jepq: Double,
    val qld: Double,
    val cash: Double = 0.0
)

data class ThreeAssetRetirementInput(
    val totalCapitalWon: Long,
    val monthlyExpenseWon: Long,
    val allocation: ThreeAssetAllocation,
    val exchangeRate: Double,
    val schdPrice: Double,
    val jepqPrice: Double,
    val qldPrice: Double,
    val schdYield: Double,
    val schdDividendGrowth: Double,
    val schdPriceGrowth: Double,
    val jepqYield: Double,
    val jepqDividendGrowth: Double,
    val jepqPriceGrowth: Double,
    val qldPriceGrowth: Double,
    val cashYield: Double,
    val inflationRate: Double,
    val taxAndInsuranceRate: Double,
    val stressTestEnabled: Boolean
)

data class ThreeAssetAnnualRow(
    val year: Int,
    val schdAssetWon: Long,
    val jepqAssetWon: Long,
    val qldAssetWon: Long,
    val cashWon: Long,
    val grossAnnualDividendWon: Long,
    val netAnnualDividendWon: Long,
    val annualExpenseWon: Long,
    val actualAnnualCashFlowWon: Long,
    val action: String,
    val totalAssetWon: Long
)

data class ThreeAssetRetirementResult(
    val initialSchdShares: Double,
    val initialJepqShares: Double,
    val initialQldShares: Double,
    val cashDepletedYear: Int?,
    val finalAssetWon: Long,
    val inflationTargetWon: Long,
    val realGrowthPercent: Double,
    val rows: List<ThreeAssetAnnualRow>
)

object ThreeAssetRetirementEngine {
    fun calculate(input: ThreeAssetRetirementInput, years: Int = 20): ThreeAssetRetirementResult {
        val initialSchdValue = input.totalCapitalWon * input.allocation.schd
        val initialJepqValue = input.totalCapitalWon * input.allocation.jepq
        val initialQldValue = input.totalCapitalWon * input.allocation.qld
        var cashValue = input.totalCapitalWon * input.allocation.cash

        val schdShares = initialSchdValue / input.exchangeRate / input.schdPrice
        val jepqShares = initialJepqValue / input.exchangeRate / input.jepqPrice
        val qldShares = initialQldValue / input.exchangeRate / input.qldPrice

        var schdMultiplier = 1.0
        var jepqMultiplier = 1.0
        var qldMultiplier = 1.0
        var annualExpense = input.monthlyExpenseWon * 12.0
        var cashDepletedYear: Int? = null
        var finalYearTotal = 0.0
        var currentSchdPrice = input.schdPrice
        var currentJepqPrice = input.jepqPrice
        var currentQldPrice = input.qldPrice
        val rows = mutableListOf<ThreeAssetAnnualRow>()

        for (year in 1..years.coerceAtLeast(0)) {
            if (input.stressTestEnabled && year == 1) {
                currentSchdPrice = input.schdPrice * 0.7
                currentJepqPrice = input.jepqPrice * 0.7
                currentQldPrice = input.qldPrice * 0.4
            } else if (!input.stressTestEnabled || year > 3) {
                currentSchdPrice *= 1.0 + input.schdPriceGrowth
                currentJepqPrice *= 1.0 + input.jepqPriceGrowth
                currentQldPrice *= 1.0 + input.qldPriceGrowth
            }

            var schdValue = schdShares * schdMultiplier * currentSchdPrice * input.exchangeRate
            var jepqValue = jepqShares * jepqMultiplier * currentJepqPrice * input.exchangeRate
            var qldValue = qldShares * qldMultiplier * currentQldPrice * input.exchangeRate
            var availableCash = cashValue

            fun dividendFactor(growth: Double, cutDuringStress: Boolean): Double {
                if (!input.stressTestEnabled) return (1.0 + growth).pow((year - 1).toDouble())
                if (year <= 3) return if (cutDuringStress) 0.8 else 1.0
                return (1.0 + growth).pow((year - 3).toDouble())
            }

            val schdDividendFactor = dividendFactor(input.schdDividendGrowth, cutDuringStress = false)
            val jepqDividendFactor = dividendFactor(input.jepqDividendGrowth, cutDuringStress = true)
            val grossDividend =
                schdShares * schdMultiplier * input.schdPrice * input.schdYield * schdDividendFactor * input.exchangeRate +
                    jepqShares * jepqMultiplier * input.jepqPrice * input.jepqYield * jepqDividendFactor * input.exchangeRate +
                    availableCash.coerceAtLeast(0.0) * input.cashYield
            val netDividend = grossDividend * (1.0 - input.taxAndInsuranceRate)
            val actualAnnualCashFlow = minOf(
                annualExpense.coerceAtLeast(0.0),
                (netDividend + availableCash + schdValue + jepqValue + qldValue).coerceAtLeast(0.0)
            )
            val shortfall = annualExpense - netDividend
            val actions = mutableListOf<String>()

            if (shortfall > 0.0) {
                var remainingShortfall = shortfall
                val cashDrawn = minOf(availableCash, remainingShortfall)
                if (cashDrawn > 0.0) {
                    availableCash -= cashDrawn
                    remainingShortfall -= cashDrawn
                    actions += "현금인출: ${formatManWon(cashDrawn)}"
                }
                if (remainingShortfall > 0.0 && cashDepletedYear == null) cashDepletedYear = year

                fun sellAsset(label: String, availableValue: Double, update: (Double) -> Unit): Double {
                    if (remainingShortfall <= 0.0 || availableValue <= 0.0) return availableValue
                    val sale = minOf(availableValue, remainingShortfall)
                    update(sale)
                    remainingShortfall -= sale
                    actions += "$label 매도: ${formatManWon(sale)}"
                    return availableValue - sale
                }

                schdValue = sellAsset("SCHD", schdValue) { sale ->
                    schdMultiplier *= 1.0 - sale / schdValue
                }
                jepqValue = sellAsset("JEPQ", jepqValue) { sale ->
                    jepqMultiplier *= 1.0 - sale / jepqValue
                }
                qldValue = sellAsset("QLD", qldValue) { sale ->
                    qldMultiplier *= 1.0 - sale / qldValue
                }
                if (remainingShortfall > 0.0) actions += "계좌 소진"
            } else {
                val surplus = abs(shortfall)
                availableCash += surplus
                actions += "버퍼 재적립: ${formatManWon(surplus)}"
            }

            cashValue = availableCash
            val endTotal = schdValue + jepqValue + qldValue + cashValue
            finalYearTotal = endTotal
            rows += ThreeAssetAnnualRow(
                year = year,
                schdAssetWon = schdValue.roundToLong().coerceAtLeast(0L),
                jepqAssetWon = jepqValue.roundToLong().coerceAtLeast(0L),
                qldAssetWon = qldValue.roundToLong().coerceAtLeast(0L),
                cashWon = cashValue.roundToLong().coerceAtLeast(0L),
                grossAnnualDividendWon = grossDividend.roundToLong().coerceAtLeast(0L),
                netAnnualDividendWon = netDividend.roundToLong().coerceAtLeast(0L),
                annualExpenseWon = annualExpense.roundToLong().coerceAtLeast(0L),
                actualAnnualCashFlowWon = actualAnnualCashFlow.roundToLong().coerceAtLeast(0L),
                action = actions.joinToString(" / "),
                totalAssetWon = endTotal.roundToLong().coerceAtLeast(0L)
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
            initialSchdShares = schdShares,
            initialJepqShares = jepqShares,
            initialQldShares = qldShares,
            cashDepletedYear = cashDepletedYear,
            finalAssetWon = finalYearTotal.roundToLong().coerceAtLeast(0L),
            inflationTargetWon = inflationTarget.roundToLong().coerceAtLeast(0L),
            realGrowthPercent = realGrowthPercent,
            rows = rows
        )
    }

    private fun formatManWon(value: Double): String =
        "${NumberFormat.getNumberInstance(Locale.KOREA).format((value / 10_000.0).roundToLong())}만"
}
