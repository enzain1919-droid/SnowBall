package com.dg479.longrunportfolio.simulation

import java.time.LocalDate
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HistoricalRateEngineTest {
    @Test
    fun calculatesFullHistoryPriceCagrAndLatestYield() {
        val result = HistoricalRateEngine.calculate(
            rawPrices = listOf(
                HistoricalPricePoint(LocalDate.of(2020, 1, 1), 100.0),
                HistoricalPricePoint(LocalDate.of(2025, 1, 1), 200.0)
            ),
            rawDividends = (1..4).map { quarter ->
                HistoricalDividendPoint(LocalDate.of(2024, quarter * 3, 1), 2.0)
            }
        )!!

        assertEquals(14.87, result.priceCagrPercent, 0.02)
        assertEquals(4.0, result.dividendYieldPercent!!, 1e-9)
        assertEquals(200.0, result.latestPrice, 0.0)
    }

    @Test
    fun calculatesDividendGrowthFromFirstComparableYearToLatest() {
        val dividends = (2020..2024).flatMap { year ->
            val annualAmount = 4.0 * (1.10).pow(year - 2020)
            (1..4).map { quarter ->
                HistoricalDividendPoint(LocalDate.of(year, quarter * 3, 1), annualAmount / 4.0)
            }
        }
        val result = HistoricalRateEngine.calculate(
            rawPrices = listOf(
                HistoricalPricePoint(LocalDate.of(2020, 1, 1), 100.0),
                HistoricalPricePoint(LocalDate.of(2025, 1, 1), 150.0)
            ),
            rawDividends = dividends
        )!!

        assertEquals(10.0, result.dividendGrowthCagrPercent!!, 0.2)
    }

    @Test
    fun rejectsLessThanOneYearOfPrices() {
        assertNull(
            HistoricalRateEngine.calculate(
                listOf(
                    HistoricalPricePoint(LocalDate.of(2025, 1, 1), 100.0),
                    HistoricalPricePoint(LocalDate.of(2025, 6, 1), 110.0)
                )
            )
        )
    }
}
