package com.dg479.longrunportfolio.simulation

import java.time.LocalDate

internal object FullHistoryRateMath {
    fun averageAnnualDividendYieldPercent(
        prices: List<Pair<LocalDate, Double>>,
        dividends: List<HistoricalDividendPoint>
    ): Double? {
        if (prices.isEmpty() || dividends.isEmpty()) return null

        val firstComparableDate = prices.first().first.plusYears(1)
        val annualPriceObservations = prices
            .groupBy { (date, _) -> date.year }
            .values
            .map { yearlyPrices -> yearlyPrices.maxBy { (date, _) -> date } }
            .filter { (date, price) -> !date.isBefore(firstComparableDate) && price > 0.0 }
            .sortedBy { (date, _) -> date }

        if (annualPriceObservations.isEmpty()) return null
        return annualPriceObservations
            .map { (date, price) ->
                trailingAnnualDividend(dividends, date) / price * 100.0
            }
            .filter { it.isFinite() && it >= 0.0 }
            .average()
            .takeIf { it.isFinite() }
    }

    fun trailingAnnualDividend(
        points: List<HistoricalDividendPoint>,
        endDate: LocalDate
    ): Double {
        val startExclusive = endDate.minusYears(1)
        return points
            .filter { it.date.isAfter(startExclusive) && !it.date.isAfter(endDate) }
            .sumOf { it.amount }
    }
}
