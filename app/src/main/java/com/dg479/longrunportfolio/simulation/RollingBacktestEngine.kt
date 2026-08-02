package com.dg479.longrunportfolio.simulation

import kotlin.math.pow
import kotlin.math.roundToLong

data class RollingBacktestWindow(
    val startYear: Int,
    val endYear: Int
)

data class RollingBacktestSummary(
    val startYear: Int,
    val endYear: Int,
    val annualizedReturnPercent: Double,
    val maxDrawdownPercent: Double,
    val finalAssetWon: Long,
    val inflationAdjustedFinalAssetWon: Long,
    val usedHistoricalData: Boolean
)

object RollingBacktestEngine {
    fun windows(
        startYear: Int,
        intervalYears: Int,
        currentYear: Int
    ): List<RollingBacktestWindow> {
        if (intervalYears <= 0 || startYear > currentYear) return emptyList()
        val lastStartYear = currentYear.toLong() - intervalYears.toLong() + 1L
        if (startYear.toLong() > lastStartYear) return emptyList()

        return (startYear.toLong()..lastStartYear).map { windowStart ->
            RollingBacktestWindow(
                startYear = windowStart.toInt(),
                endYear = (windowStart + intervalYears - 1L).toInt()
            )
        }
    }

    fun summarize(
        window: RollingBacktestWindow,
        monthlyReturns: List<Double>,
        monthlyDrawdowns: List<Double>,
        finalAssetWon: Long,
        annualInflationRatesPercent: Map<Int, Double>,
        usedHistoricalData: Boolean
    ): RollingBacktestSummary = RollingBacktestSummary(
        startYear = window.startYear,
        endYear = window.endYear,
        annualizedReturnPercent = annualizedReturnPercent(monthlyReturns),
        maxDrawdownPercent = monthlyDrawdowns
            .filter(Double::isFinite)
            .minOrNull()
            ?.coerceAtMost(0.0)
            ?: 0.0,
        finalAssetWon = finalAssetWon,
        inflationAdjustedFinalAssetWon = inflationAdjustedValue(
            value = finalAssetWon,
            startYear = window.startYear,
            endYear = window.endYear,
            annualInflationRatesPercent = annualInflationRatesPercent
        ),
        usedHistoricalData = usedHistoricalData
    )

    fun annualizedReturnPercent(monthlyReturns: List<Double>): Double {
        val validReturns = monthlyReturns
            .drop(1)
            .filter(Double::isFinite)
        if (validReturns.isEmpty()) return 0.0

        val growthFactor = validReturns.fold(1.0) { accumulated, monthlyReturn ->
            accumulated * (1.0 + monthlyReturn.coerceAtLeast(-0.999999))
        }
        if (!growthFactor.isFinite() || growthFactor <= 0.0) return -100.0
        return (growthFactor.pow(12.0 / validReturns.size.toDouble()) - 1.0) * 100.0
    }

    fun inflationAdjustedValue(
        value: Long,
        startYear: Int,
        endYear: Int,
        annualInflationRatesPercent: Map<Int, Double>
    ): Long {
        if (value <= 0L || startYear > endYear) return value.coerceAtLeast(0L)

        val finiteRates = annualInflationRatesPercent
            .filterValues { it.isFinite() }
            .toSortedMap()
        val earliestFiveRateAverage = finiteRates.values
            .take(5)
            .average()
            .takeIf { it.isFinite() }
            ?: 2.0

        val factor = (startYear..endYear).fold(1.0) { accumulated, year ->
            val rate = finiteRates[year] ?: when {
                finiteRates.isEmpty() -> 2.0
                year < finiteRates.firstKey() -> earliestFiveRateAverage
                else -> finiteRates
                    .filterKeys { it < year }
                    .values
                    .toList()
                    .takeLast(5)
                    .average()
                    .takeIf { it.isFinite() }
                    ?: 2.0
            }
            accumulated * (1.0 + rate.coerceIn(-99.0, 100.0) / 100.0)
        }
        val adjusted = if (factor.isFinite() && factor > 0.0) {
            value.toDouble() / factor
        } else {
            value.toDouble()
        }
        return when {
            !adjusted.isFinite() || adjusted >= Long.MAX_VALUE.toDouble() -> Long.MAX_VALUE
            adjusted <= 0.0 -> 0L
            else -> adjusted.roundToLong()
        }
    }
}

/**
 * World Bank FP.CPI.TOTL.ZG annual CPI inflation for Korea, Rep.
 * This bundled series keeps inflation adjustment available offline. Values fetched at runtime
 * replace matching years, and years after the latest published observation use the recent
 * five-year average in [RollingBacktestEngine.inflationAdjustedValue].
 */
val KoreanAnnualInflationFallbackPercent: Map<Int, Double> = mapOf(
    1960 to 7.965566,
    1961 to 8.195653,
    1962 to 6.618311,
    1963 to 20.691647,
    1964 to 29.462831,
    1965 to 13.548195,
    1966 to 11.261385,
    1967 to 10.882440,
    1968 to 10.771821,
    1969 to 12.389565,
    1970 to 15.950342,
    1971 to 13.511702,
    1972 to 11.688987,
    1973 to 3.221029,
    1974 to 24.304200,
    1975 to 25.249256,
    1976 to 15.327321,
    1977 to 10.096665,
    1978 to 14.460285,
    1979 to 18.323496,
    1980 to 28.697609,
    1981 to 21.351640,
    1982 to 7.190847,
    1983 to 3.420614,
    1984 to 2.273956,
    1985 to 2.459109,
    1986 to 2.749981,
    1987 to 3.049661,
    1988 to 7.146098,
    1989 to 5.700165,
    1990 to 8.573272,
    1991 to 9.332790,
    1992 to 6.213281,
    1993 to 4.800997,
    1994 to 6.265860,
    1995 to 4.480741,
    1996 to 4.924544,
    1997 to 4.438932,
    1998 to 7.513580,
    1999 to 0.812957,
    2000 to 2.259166,
    2001 to 4.066576,
    2002 to 2.762262,
    2003 to 3.514875,
    2004 to 3.590663,
    2005 to 2.753792,
    2006 to 2.242340,
    2007 to 2.534574,
    2008 to 4.673897,
    2009 to 2.756497,
    2010 to 2.939287,
    2011 to 4.025965,
    2012 to 2.187071,
    2013 to 1.301348,
    2014 to 1.274774,
    2015 to 0.706332,
    2016 to 0.971686,
    2017 to 1.944332,
    2018 to 1.475839,
    2019 to 0.383000,
    2020 to 0.537288,
    2021 to 2.498333,
    2022 to 5.089514,
    2023 to 3.597456,
    2024 to 2.321743
)
