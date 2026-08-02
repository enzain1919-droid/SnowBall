package com.dg479.longrunportfolio.simulation

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

data class HistoricalClosePoint(
    val date: LocalDate,
    val close: Double
)

data class ThreeAssetHistoricalRates(
    val latestPrice: Double,
    val averageDividendYieldPercent: Double?,
    val dividendGrowthCagrPercent: Double?,
    val priceOnlyCagrPercent: Double,
    val priceStartDate: LocalDate,
    val priceEndDate: LocalDate
)

object ThreeAssetHistoricalRateEngine {
    fun calculate(
        rawPrices: List<HistoricalClosePoint>,
        rawDividends: List<HistoricalDividendPoint> = emptyList()
    ): ThreeAssetHistoricalRates? {
        val prices = rawPrices
            .filter { it.close > 0.0 }
            .distinctBy { it.date }
            .sortedBy { it.date }
        val firstPrice = prices.firstOrNull() ?: return null
        val lastPrice = prices.lastOrNull() ?: return null
        val priceYears = yearsBetween(firstPrice.date, lastPrice.date)
        if (prices.size < 2 || priceYears < 1.0) return null

        val priceOnlyCagr = (lastPrice.close / firstPrice.close)
            .pow(1.0 / priceYears) - 1.0
        if (!priceOnlyCagr.isFinite()) return null

        val dividends = rawDividends
            .filter { it.amount > 0.0 && !it.date.isAfter(lastPrice.date) }
            .distinctBy { it.date }
            .sortedBy { it.date }
        val firstDividend = dividends.firstOrNull()
        val averageDividendYield = firstDividend?.let { first ->
            prices
                .groupBy { it.date.year }
                .values
                .mapNotNull { yearlyPrices ->
                    val yearEndPrice = yearlyPrices.maxBy { it.date }
                    if (yearEndPrice.date.isBefore(first.date.plusYears(1))) return@mapNotNull null
                    val trailingDividend = trailingDividend(dividends, yearEndPrice.date)
                    if (trailingDividend <= 0.0) return@mapNotNull null
                    (trailingDividend / yearEndPrice.close * 100.0)
                        .takeIf { it.isFinite() && it >= 0.0 }
                }
                .average()
                .takeIf { it.isFinite() }
        }

        val dividendGrowth = firstDividend?.date?.plusYears(1)?.let { firstComparableDate ->
            val lastDividendDate = dividends.lastOrNull()?.date ?: return@let null
            val dividendYears = yearsBetween(firstComparableDate, lastDividendDate)
            val firstAnnualDividend = trailingDividend(dividends, firstComparableDate)
            val lastAnnualDividend = trailingDividend(dividends, lastDividendDate)
            if (dividendYears >= 1.0 && firstAnnualDividend > 0.0 && lastAnnualDividend > 0.0) {
                ((lastAnnualDividend / firstAnnualDividend).pow(1.0 / dividendYears) - 1.0)
                    .takeIf { it.isFinite() }
                    ?.times(100.0)
            } else {
                null
            }
        }

        return ThreeAssetHistoricalRates(
            latestPrice = lastPrice.close,
            averageDividendYieldPercent = averageDividendYield,
            dividendGrowthCagrPercent = dividendGrowth,
            priceOnlyCagrPercent = priceOnlyCagr * 100.0,
            priceStartDate = firstPrice.date,
            priceEndDate = lastPrice.date
        )
    }

    private fun trailingDividend(points: List<HistoricalDividendPoint>, endDate: LocalDate): Double {
        val startExclusive = endDate.minusYears(1)
        return points
            .filter { it.date.isAfter(startExclusive) && !it.date.isAfter(endDate) }
            .sumOf { it.amount }
    }

    private fun yearsBetween(start: LocalDate, end: LocalDate): Double =
        ChronoUnit.DAYS.between(start, end).toDouble() / 365.25
}
