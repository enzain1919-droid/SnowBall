package com.dg479.longrunportfolio.simulation

enum class ScenarioComparisonType {
    DIVIDEND,
    SELF_DIVIDEND,
    THREE_ASSET
}

data class ScenarioComparisonPoint(
    val year: Int,
    val monthlyCashFlowWon: Long,
    val totalAssetWon: Long
)

data class ScenarioComparisonSeries(
    val id: String,
    val name: String,
    val type: ScenarioComparisonType,
    val points: List<ScenarioComparisonPoint>
)

data class ScenarioComparisonSummary(
    val id: String,
    val yearOneMonthlyCashFlowWon: Long,
    val yearTenMonthlyCashFlowWon: Long,
    val yearTwentyMonthlyCashFlowWon: Long,
    val finalAssetWon: Long,
    val cumulativeCashFlowWon: Long
)

object ScenarioComparisonEngine {
    fun summarize(series: ScenarioComparisonSeries): ScenarioComparisonSummary? {
        val points = series.points.sortedBy { it.year }
        if (points.isEmpty()) return null

        fun cashFlowAt(year: Int): Long = points.firstOrNull { it.year == year }?.monthlyCashFlowWon ?: 0L
        return ScenarioComparisonSummary(
            id = series.id,
            yearOneMonthlyCashFlowWon = cashFlowAt(1),
            yearTenMonthlyCashFlowWon = cashFlowAt(10),
            yearTwentyMonthlyCashFlowWon = cashFlowAt(20),
            finalAssetWon = points.last().totalAssetWon,
            cumulativeCashFlowWon = points.sumOf { point ->
                (point.monthlyCashFlowWon * 12L).coerceAtLeast(0L)
            }
        )
    }

    fun pointAt(series: ScenarioComparisonSeries, year: Int): ScenarioComparisonPoint? =
        series.points.firstOrNull { it.year == year }
}
