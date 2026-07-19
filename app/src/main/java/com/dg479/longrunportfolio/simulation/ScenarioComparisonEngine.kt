package com.dg479.longrunportfolio.simulation

enum class ScenarioComparisonType {
    DIVIDEND,
    SELF_DIVIDEND,
    THREE_ASSET
}

data class ScenarioComparisonPoint(
    val year: Int,
    val monthlyCashFlowWon: Long,
    val totalAssetWon: Long,
    val annualPortfolioOutflowWon: Long = monthlyCashFlowWon * 12L
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
    fun fromThreeAssetRows(
        id: String,
        name: String,
        rows: List<ThreeAssetAnnualRow>
    ): ScenarioComparisonSeries = ScenarioComparisonSeries(
        id = id,
        name = name,
        type = ScenarioComparisonType.THREE_ASSET,
        points = rows.map { row ->
            ScenarioComparisonPoint(
                year = row.year,
                monthlyCashFlowWon = row.netAnnualDividendWon / 12L,
                totalAssetWon = row.totalAssetWon,
                annualPortfolioOutflowWon = row.actualAnnualCashFlowWon
            )
        }
    )

    fun inferredAnnualReturnPercent(series: ScenarioComparisonSeries): Double? {
        val annualReturns = series.points
            .sortedBy { it.year }
            .zipWithNext()
            .mapNotNull { (previous, current) ->
                if (previous.totalAssetWon <= 0L) return@mapNotNull null
                ((current.totalAssetWon + current.annualPortfolioOutflowWon) /
                    previous.totalAssetWon.toDouble() - 1.0)
                    .takeIf { it.isFinite() }
            }
        return annualReturns.average().takeIf { it.isFinite() }?.times(100.0)
    }

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
