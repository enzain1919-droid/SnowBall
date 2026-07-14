package com.dg479.longrunportfolio.simulation

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.sqrt

data class HistoricalAssetAllocation(
    val ticker: String,
    val weight: Double
)

data class HistoricalPricePoint(
    val date: LocalDate,
    val adjustedClose: Double
)

data class HistoricalVolatilityEstimate(
    val annualizedVolatilityPercent: Double,
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val monthlyObservationCount: Int,
    val tickers: List<String>
)

object HistoricalVolatilityEngine {
    private const val MinimumMonthlyObservations = 12

    fun calculate(
        rawAllocations: List<HistoricalAssetAllocation>,
        priceHistory: Map<String, List<HistoricalPricePoint>>
    ): HistoricalVolatilityEstimate? {
        val allocations = rawAllocations
            .filter { it.ticker.isNotBlank() && it.weight > 0.0 }
            .groupBy { it.ticker.trim().uppercase() }
            .map { (ticker, entries) -> HistoricalAssetAllocation(ticker, entries.sumOf { it.weight }) }
        val totalWeight = allocations.sumOf { it.weight }
        if (allocations.isEmpty() || totalWeight <= 0.0) return null

        val monthlyReturnsByTicker = allocations.associate { allocation ->
            val history = priceHistory.entries
                .firstOrNull { it.key.equals(allocation.ticker, ignoreCase = true) }
                ?.value
                .orEmpty()
            allocation.ticker to monthlyReturns(history)
        }
        if (monthlyReturnsByTicker.values.any { it.isEmpty() }) return null

        val commonMonths = monthlyReturnsByTicker.values
            .map { it.keys }
            .reduce { common, months -> common.intersect(months) }
            .sorted()
        if (commonMonths.size < MinimumMonthlyObservations) return null

        val normalizedWeights = allocations.associate { it.ticker to it.weight / totalWeight }
        val portfolioReturns = commonMonths.map { month ->
            normalizedWeights.entries.sumOf { (ticker, weight) ->
                weight * monthlyReturnsByTicker.getValue(ticker).getValue(month)
            }
        }
        val average = portfolioReturns.average()
        val variance = portfolioReturns.sumOf { returnValue ->
            val difference = returnValue - average
            difference * difference
        } / (portfolioReturns.size - 1)

        return HistoricalVolatilityEstimate(
            annualizedVolatilityPercent = sqrt(variance) * sqrt(12.0) * 100.0,
            startMonth = commonMonths.first(),
            endMonth = commonMonths.last(),
            monthlyObservationCount = commonMonths.size,
            tickers = allocations.map { it.ticker }
        )
    }

    private fun monthlyReturns(points: List<HistoricalPricePoint>): Map<YearMonth, Double> {
        val monthlyCloses = points
            .filter { it.adjustedClose > 0.0 }
            .groupBy { YearMonth.from(it.date) }
            .mapValues { (_, entries) -> entries.maxBy { it.date }.adjustedClose }
            .toSortedMap()

        return monthlyCloses.entries.zipWithNext().mapNotNull { (previous, current) ->
            if (ChronoUnit.MONTHS.between(previous.key, current.key) != 1L) return@mapNotNull null
            val monthlyReturn = current.value / previous.value - 1.0
            current.key to monthlyReturn.takeIf { it.isFinite() }
        }.mapNotNull { (month, monthlyReturn) ->
            monthlyReturn?.let { month to it }
        }.toMap()
    }
}
