package com.dg479.longrunportfolio.simulation

import java.time.LocalDate
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Test

class ThreeAssetHistoricalRateEngineTest {
    @Test
    fun calculatesAverageHistoricalDividendYieldFromAnnualObservations() {
        val prices = listOf(
            HistoricalClosePoint(LocalDate.of(2020, 1, 1), 100.0),
            HistoricalClosePoint(LocalDate.of(2021, 12, 31), 100.0),
            HistoricalClosePoint(LocalDate.of(2022, 12, 31), 200.0)
        )
        val dividends = (2020..2022).flatMap { year ->
            val annualDividend = if (year == 2022) 8.0 else 4.0
            (1..4).map { quarter ->
                HistoricalDividendPoint(LocalDate.of(year, quarter * 3, 1), annualDividend / 4.0)
            }
        }

        val result = ThreeAssetHistoricalRateEngine.calculate(prices, dividends)!!

        assertEquals(4.0, result.averageDividendYieldPercent!!, 1e-9)
    }

    @Test
    fun averageDividendYieldIncludesCompletedListingYearsBeforeFirstDividend() {
        val prices = listOf(
            HistoricalClosePoint(LocalDate.of(2020, 1, 1), 100.0),
            HistoricalClosePoint(LocalDate.of(2021, 12, 31), 100.0),
            HistoricalClosePoint(LocalDate.of(2022, 12, 31), 100.0)
        )
        val dividends = (1..4).map { quarter ->
            HistoricalDividendPoint(LocalDate.of(2022, quarter * 3, 1), 1.0)
        }

        val result = ThreeAssetHistoricalRateEngine.calculate(prices, dividends)!!

        assertEquals(2.0, result.averageDividendYieldPercent!!, 1e-9)
    }

    @Test
    fun priceGrowthUsesProvidedPriceOnlyCloseSeries() {
        val result = ThreeAssetHistoricalRateEngine.calculate(
            rawPrices = listOf(
                HistoricalClosePoint(LocalDate.of(2020, 1, 1), 100.0),
                HistoricalClosePoint(LocalDate.of(2025, 1, 1), 150.0)
            )
        )!!

        val expected = ((150.0 / 100.0).pow(1.0 / 5.0) - 1.0) * 100.0
        assertEquals(expected, result.priceOnlyCagrPercent, 0.02)
    }
}
