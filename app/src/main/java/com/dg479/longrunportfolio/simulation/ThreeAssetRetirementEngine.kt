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

            val schdDividendFactor: Double
            val jepqDividendFactor: Double
            if (input.stressTestEnabled) {
                if (year <= 3) {
                    schdDividendFactor = 1.0
                    jepqDividendFactor = 0.8
                } else {
                    schdDividendFactor = (1.0 + input.schdDividendGrowth).pow((year - 3).toDouble())
                    jepqDividendFactor = (1.0 + input.jepqDividendGrowth).pow((year - 3).toDouble())
                }
            } else {
                schdDividendFactor = (1.0 + input.schdDividendGrowth).pow((year - 1).toDouble())
                jepqDividendFactor = (1.0 + input.jepqDividendGrowth).pow((year - 1).toDouble())
            }

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
            val action: String

            if (shortfall > 0.0) {
                if (availableCash >= shortfall) {
                    availableCash -= shortfall
                    action = "현금인출: ${formatManWon(shortfall)}"
                } else {
                    var remainingShortfall = shortfall - availableCash
                    val cashDrawn = availableCash
                    availableCash = 0.0
                    if (cashDepletedYear == null) cashDepletedYear = year

                    if (schdValue >= remainingShortfall && schdValue > 0.0) {
                        schdMultiplier *= 1.0 - remainingShortfall / schdValue
                        schdValue -= remainingShortfall
                        action = "현금인출: ${formatManWon(cashDrawn)} / SCHD 매도: ${formatManWon(remainingShortfall)}"
                    } else {
                        remainingShortfall -= schdValue
                        val schdDrawn = schdValue
                        schdMultiplier = 0.0
                        schdValue = 0.0

                        if (jepqValue >= remainingShortfall && jepqValue > 0.0) {
                            jepqMultiplier *= 1.0 - remainingShortfall / jepqValue
                            jepqValue -= remainingShortfall
                            action = "SCHD 전액매도: ${formatManWon(schdDrawn)} / JEPQ 매도: ${formatManWon(remainingShortfall)}"
                        } else {
                            remainingShortfall -= jepqValue
                            val jepqDrawn = jepqValue
                            jepqMultiplier = 0.0
                            jepqValue = 0.0

                            if (qldValue >= remainingShortfall && qldValue > 0.0) {
                                qldMultiplier *= 1.0 - remainingShortfall / qldValue
                                qldValue -= remainingShortfall
                                action = "JEPQ 전액매도: ${formatManWon(jepqDrawn)} / QLD 매도: ${formatManWon(remainingShortfall)}"
                            } else {
                                qldMultiplier = 0.0
                                qldValue = 0.0
                                action = "계좌 파산"
                            }
                        }
                    }
                }
            } else {
                val surplus = abs(shortfall)
                availableCash += surplus
                action = "버퍼 재적립: ${formatManWon(surplus)}"
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
                action = action,
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
