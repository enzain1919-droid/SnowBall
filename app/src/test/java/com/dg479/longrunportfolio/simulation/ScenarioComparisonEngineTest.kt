package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScenarioComparisonEngineTest {
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
}
