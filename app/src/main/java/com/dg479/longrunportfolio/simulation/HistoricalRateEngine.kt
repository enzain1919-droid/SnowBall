package com.dg479.longrunportfolio.simulation

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

data class HistoricalDividendPoint(
    val date: LocalDate,
    val amount: Double
)

data class HistoricalAnnualRates(
    val latestPrice: Double,
    val priceCagrPercent: Double,
    val priceStartDate: LocalDate,
    val priceEndDate: LocalDate,
    val dividendYieldPercent: Double?,
    val dividendGrowthCagrPercent: Double?,
    val dividendStartDate: LocalDate?,
    val dividendEndDate: LocalDate?
)

object HistoricalRateEngine {
    fun calculate(
        rawPrices: List<HistoricalPricePoint>,
        rawDividends: List<HistoricalDividendPoint> = emptyList()
    ): HistoricalAnnualRates? {
        val prices = rawPrices
            .filter { it.adjustedClose > 0.0 }
            .distinctBy { it.date }
            .sortedBy { it.date }
        val firstPrice = prices.firstOrNull() ?: return null
        val lastPrice = prices.lastOrNull() ?: return null
        val priceYears = yearsBetween(firstPrice.date, lastPrice.date)
        if (prices.size < 2 || priceYears < 1.0) return null

        val priceCagr = (lastPrice.adjustedClose / firstPrice.adjustedClose)
            .pow(1.0 / priceYears) - 1.0
        if (!priceCagr.isFinite()) return null

        val dividends = rawDividends
            .filter { it.amount > 0.0 && !it.date.isAfter(lastPrice.date) }
            .distinctBy { it.date }
            .sortedBy { it.date }
        val trailingDividend = trailingDividend(dividends, lastPrice.date)
        val dividendYield = trailingDividend
            .takeIf { it > 0.0 }
            ?.let { it / lastPrice.adjustedClose * 100.0 }
        val firstDividend = dividends.firstOrNull()
        val dividendStartDate = firstDividend?.date?.plusYears(1)
        val dividendEndDate = dividends.lastOrNull()?.date
        val dividendGrowth = if (
            dividendStartDate != null &&
            dividendEndDate != null &&
            dividendEndDate.isAfter(dividendStartDate)
        ) {
            val dividendYears = yearsBetween(dividendStartDate, dividendEndDate)
            val startDividend = trailingDividend(dividends, dividendStartDate)
            val endDividend = trailingDividend(dividends, dividendEndDate)
            if (dividendYears >= 1.0 && startDividend > 0.0 && endDividend > 0.0) {
                ((endDividend / startDividend).pow(1.0 / dividendYears) - 1.0)
                    .takeIf { it.isFinite() }
                    ?.times(100.0)
            } else {
                null
            }
        } else {
            null
        }

        return HistoricalAnnualRates(
            latestPrice = lastPrice.adjustedClose,
            priceCagrPercent = priceCagr * 100.0,
            priceStartDate = firstPrice.date,
            priceEndDate = lastPrice.date,
            dividendYieldPercent = dividendYield,
            dividendGrowthCagrPercent = dividendGrowth,
            dividendStartDate = dividendStartDate,
            dividendEndDate = dividendEndDate
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
