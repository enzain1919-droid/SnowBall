package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.sqrt

class HistoricalVolatilityEngineTest {
    @Test
    fun annualizesMonthlySampleVolatility() {
        val monthlyReturns = listOf(
            -0.04, 0.02, 0.06, -0.01, 0.03, 0.05,
            -0.02, 0.01, 0.04, -0.03, 0.02, 0.07
        )
        val estimate = HistoricalVolatilityEngine.calculate(
            rawAllocations = listOf(HistoricalAssetAllocation("AAA", 1.0)),
            priceHistory = mapOf("AAA" to pricesFromReturns(monthlyReturns))
        )!!
        val average = monthlyReturns.average()
        val sampleVariance = monthlyReturns.sumOf { (it - average) * (it - average) } /
            (monthlyReturns.size - 1)
        val expected = sqrt(sampleVariance) * sqrt(12.0) * 100.0

        assertEquals(expected, estimate.annualizedVolatilityPercent, 1e-9)
        assertEquals(12, estimate.monthlyObservationCount)
    }

    @Test
    fun calculatesMaximumDrawdownFromPortfolioMonthlyReturns() {
        val monthlyReturns = listOf(0.10, -0.20) + List(10) { 0.03 }
        val estimate = HistoricalVolatilityEngine.calculate(
            rawAllocations = listOf(HistoricalAssetAllocation("AAA", 1.0)),
            priceHistory = mapOf("AAA" to pricesFromReturns(monthlyReturns))
        )!!

        assertEquals(20.0, estimate.maximumDrawdownPercent, 1e-9)
    }

    @Test
    fun combinesAssetsUsingSavedWeightsAndCommonMonths() {
        val firstReturns = List(12) { index -> if (index % 2 == 0) 0.10 else -0.05 }
        val secondReturns = List(12) { index -> if (index % 2 == 0) -0.02 else 0.04 }
        val estimate = HistoricalVolatilityEngine.calculate(
            rawAllocations = listOf(
                HistoricalAssetAllocation("AAA", 75.0),
                HistoricalAssetAllocation("BBB", 25.0)
            ),
            priceHistory = mapOf(
                "AAA" to pricesFromReturns(firstReturns),
                "BBB" to pricesFromReturns(secondReturns)
            )
        )!!
        val weightedReturns = firstReturns.indices.map { index ->
            firstReturns[index] * 0.75 + secondReturns[index] * 0.25
        }
        val average = weightedReturns.average()
        val expected = sqrt(weightedReturns.sumOf { (it - average) * (it - average) } / 11.0) *
            sqrt(12.0) * 100.0
        val firstCagr = HistoricalRateEngine.calculate(pricesFromReturns(firstReturns))!!.priceCagrPercent
        val secondCagr = HistoricalRateEngine.calculate(pricesFromReturns(secondReturns))!!.priceCagrPercent

        assertEquals(expected, estimate.annualizedVolatilityPercent, 1e-9)
        assertEquals(firstCagr * 0.75 + secondCagr * 0.25, estimate.annualizedReturnPercent, 1e-9)
        assertEquals(listOf("AAA", "BBB"), estimate.tickers)
    }

    @Test
    fun rejectsMissingAssetHistoryOrTooFewObservations() {
        val missingAsset = HistoricalVolatilityEngine.calculate(
            rawAllocations = listOf(
                HistoricalAssetAllocation("AAA", 0.5),
                HistoricalAssetAllocation("BBB", 0.5)
            ),
            priceHistory = mapOf("AAA" to pricesFromReturns(List(12) { 0.01 }))
        )
        val tooShort = HistoricalVolatilityEngine.calculate(
            rawAllocations = listOf(HistoricalAssetAllocation("AAA", 1.0)),
            priceHistory = mapOf("AAA" to pricesFromReturns(List(11) { 0.01 }))
        )

        assertNull(missingAsset)
        assertNull(tooShort)
        assertTrue(pricesFromReturns(List(12) { 0.01 }).all { it.adjustedClose > 0.0 })
    }

    private fun pricesFromReturns(returns: List<Double>): List<HistoricalPricePoint> {
        var close = 100.0
        val points = mutableListOf(HistoricalPricePoint(LocalDate.of(2020, 1, 31), close))
        returns.forEachIndexed { index, monthlyReturn ->
            close *= 1.0 + monthlyReturn
            points += HistoricalPricePoint(LocalDate.of(2020, 1, 31).plusMonths(index + 1L), close)
        }
        return points
    }
}
