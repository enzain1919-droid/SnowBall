package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScenarioComparisonEngineTest {
    @Test
    fun threeAssetSeriesUsesAnnualNetDividendInsteadOfLivingExpense() {
        val rows = listOf(
            threeAssetRow(year = 1, netAnnualDividendWon = 66_010_000L, annualExpenseWon = 66_000_000L),
            threeAssetRow(year = 2, netAnnualDividendWon = 73_040_000L, annualExpenseWon = 67_980_000L)
        )

        val series = ScenarioComparisonEngine.fromThreeAssetRows("three:plan", "Plan", rows)

        assertEquals(66_010_000L / 12L, series.points[0].monthlyCashFlowWon)
        assertEquals(73_040_000L / 12L, series.points[1].monthlyCashFlowWon)
        assertEquals(67_980_000L, series.points[1].annualPortfolioOutflowWon)
    }

    @Test
    fun inferredAnnualReturnUsesActualPortfolioOutflow() {
        val series = ScenarioComparisonSeries(
            id = "three:plan",
            name = "Plan",
            type = ScenarioComparisonType.THREE_ASSET,
            points = listOf(
                ScenarioComparisonPoint(1, 5_000_000L, 1_000_000_000L, 60_000_000L),
                ScenarioComparisonPoint(2, 6_000_000L, 1_100_000_000L, 120_000_000L),
                ScenarioComparisonPoint(3, 7_000_000L, 1_200_000_000L, 120_000_000L)
            )
        )

        assertEquals(21.0, ScenarioComparisonEngine.inferredAnnualReturnPercent(series)!!, 1e-9)
    }

    @Test
    fun summarizeUsesComparableMilestonesAndFinalAsset() {
        val series = ScenarioComparisonSeries(
            id = "dividend:plan-a",
            name = "Plan A",
            type = ScenarioComparisonType.DIVIDEND,
            points = (1..20).map { year ->
                ScenarioComparisonPoint(
                    year = year,
                    monthlyCashFlowWon = year * 100_000L,
                    totalAssetWon = year * 100_000_000L
                )
            }
        )

        val summary = ScenarioComparisonEngine.summarize(series)!!

        assertEquals(100_000L, summary.yearOneMonthlyCashFlowWon)
        assertEquals(1_000_000L, summary.yearTenMonthlyCashFlowWon)
        assertEquals(2_000_000L, summary.yearTwentyMonthlyCashFlowWon)
        assertEquals(2_000_000_000L, summary.finalAssetWon)
        assertEquals((1L..20L).sum() * 100_000L * 12L, summary.cumulativeCashFlowWon)
    }

    @Test
    fun pointAtFindsExactYear() {
        val point = ScenarioComparisonPoint(10, 3_000_000L, 1_500_000_000L)
        val series = ScenarioComparisonSeries(
            id = "self:plan",
            name = "Plan",
            type = ScenarioComparisonType.SELF_DIVIDEND,
            points = listOf(point)
        )

        assertEquals(point, ScenarioComparisonEngine.pointAt(series, 10))
        assertNull(ScenarioComparisonEngine.pointAt(series, 9))
    }

    @Test
    fun emptySeriesHasNoSummary() {
        val series = ScenarioComparisonSeries(
            id = "three:empty",
            name = "Empty",
            type = ScenarioComparisonType.THREE_ASSET,
            points = emptyList()
        )

        assertNull(ScenarioComparisonEngine.summarize(series))
    }

    private fun threeAssetRow(
        year: Int,
        netAnnualDividendWon: Long,
        annualExpenseWon: Long
    ) = ThreeAssetAnnualRow(
        year = year,
        schdAssetWon = 0L,
        jepqAssetWon = 0L,
        qldAssetWon = 0L,
        cashWon = 0L,
        grossAnnualDividendWon = netAnnualDividendWon,
        netAnnualDividendWon = netAnnualDividendWon,
        annualExpenseWon = annualExpenseWon,
        actualAnnualCashFlowWon = annualExpenseWon,
        action = "",
        totalAssetWon = 1_000_000_000L
    )
}
