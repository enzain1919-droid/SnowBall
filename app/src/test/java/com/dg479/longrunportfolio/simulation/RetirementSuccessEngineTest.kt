package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.YearMonth

class RetirementSuccessEngineTest {
    @Test
    fun zeroSpendingAlwaysSucceeds() {
        val result = RetirementSuccessEngine.calculate(
            RetirementSuccessInput(
                initialAssetWon = 1_000_000_000L,
                monthlySpendingWon = 0L,
                expectedAnnualReturnPercent = 5.0,
                annualVolatilityPercent = 20.0,
                annualInflationPercent = 2.0,
                years = 30,
                simulations = 500
            )
        )

        assertEquals(100.0, result.successRatePercent, 0.0)
        assertEquals(100.0, result.rows.last().survivalRatePercent, 0.0)
    }

    @Test
    fun impossibleFirstWithdrawalAlwaysFails() {
        val result = RetirementSuccessEngine.calculate(
            RetirementSuccessInput(
                initialAssetWon = 100_000_000L,
                monthlySpendingWon = 20_000_000L,
                expectedAnnualReturnPercent = 0.0,
                annualVolatilityPercent = 0.0,
                annualInflationPercent = 0.0,
                years = 20,
                simulations = 500
            )
        )

        assertEquals(0.0, result.successRatePercent, 0.0)
        assertEquals(1, result.medianDepletionYear)
    }

    @Test
    fun deterministicPathMatchesAnnualCalculation() {
        val result = RetirementSuccessEngine.calculate(
            RetirementSuccessInput(
                initialAssetWon = 1_000_000_000L,
                monthlySpendingWon = 4_000_000L,
                expectedAnnualReturnPercent = 10.0,
                annualVolatilityPercent = 0.0,
                annualInflationPercent = 0.0,
                years = 3,
                simulations = 100
            )
        )

        assertEquals(1_052_000_000L, result.rows.first().medianAssetWon)
        assertEquals(1_109_200_000L, result.rows[1].medianAssetWon)
        assertEquals(1_172_120_000L, result.rows[2].medianAssetWon)
        assertEquals(100.0, result.successRatePercent, 0.0)
    }

    @Test
    fun percentileBandsStayOrdered() {
        val result = RetirementSuccessEngine.calculate(
            RetirementSuccessInput(
                initialAssetWon = 1_000_000_000L,
                monthlySpendingWon = 4_000_000L,
                expectedAnnualReturnPercent = 6.0,
                annualVolatilityPercent = 15.0,
                annualInflationPercent = 2.5,
                years = 30,
                simulations = 1_000
            )
        )

        assertTrue(result.rows.all { it.lowerAssetWon <= it.medianAssetWon })
        assertTrue(result.rows.all { it.medianAssetWon <= it.upperAssetWon })
        assertEquals(result.successRatePercent, result.rows.last().survivalRatePercent, 0.0)
    }

    @Test
    fun scenarioCalculationReproducesDeterministicPathWithoutVolatility() {
        val series = ScenarioComparisonSeries(
            id = "self:test",
            name = "자가배당 테스트",
            type = ScenarioComparisonType.SELF_DIVIDEND,
            points = listOf(
                ScenarioComparisonPoint(1, 4_000_000L, 1_052_000_000L),
                ScenarioComparisonPoint(2, 4_000_000L, 1_109_200_000L),
                ScenarioComparisonPoint(3, 4_000_000L, 1_172_120_000L)
            )
        )

        val analysis = RetirementScenarioSuccessEngine.calculate(
            series = series,
            historicalVolatility = historicalVolatility(0.0),
            simulations = 100
        )!!

        assertEquals(1_000_000_000L, analysis.inferredInitialAssetWon)
        assertEquals(1_052_000_000L, analysis.result.rows[0].medianAssetWon)
        assertEquals(1_109_200_000L, analysis.result.rows[1].medianAssetWon)
        assertEquals(1_172_120_000L, analysis.result.rows[2].medianAssetWon)
        assertEquals(100.0, analysis.result.successRatePercent, 0.0)
    }

    @Test
    fun scenarioCalculationUsesHistoricalVolatilityAndStableSeed() {
        val series = ScenarioComparisonSeries(
            id = "dividend:test",
            name = "배당 테스트",
            type = ScenarioComparisonType.DIVIDEND,
            points = (1..20).map { year ->
                ScenarioComparisonPoint(year, 3_000_000L, 1_000_000_000L + year * 30_000_000L)
            }
        )

        val volatility = historicalVolatility(18.7)
        val first = RetirementScenarioSuccessEngine.calculate(series, volatility, simulations = 500)!!
        val second = RetirementScenarioSuccessEngine.calculate(series, volatility, simulations = 500)!!

        assertEquals(18.7, first.historicalVolatility.annualizedVolatilityPercent, 0.0)
        assertEquals(first.result, second.result)
        assertTrue(first.result.rows.all { it.lowerAssetWon <= it.medianAssetWon })
        assertTrue(first.result.rows.all { it.medianAssetWon <= it.upperAssetWon })
    }

    private fun historicalVolatility(percent: Double) = HistoricalVolatilityEstimate(
        annualizedVolatilityPercent = percent,
        startMonth = YearMonth.of(2015, 1),
        endMonth = YearMonth.of(2025, 12),
        monthlyObservationCount = 132,
        tickers = listOf("TEST")
    )
}
