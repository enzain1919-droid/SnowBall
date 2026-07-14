package com.dg479.longrunportfolio.simulation

import java.util.Random
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.math.sqrt

data class RetirementSuccessInput(
    val initialAssetWon: Long,
    val monthlySpendingWon: Long,
    val expectedAnnualReturnPercent: Double,
    val annualVolatilityPercent: Double,
    val annualInflationPercent: Double,
    val years: Int,
    val simulations: Int = 5_000,
    val seed: Long = 20_260_714L
)

data class RetirementSuccessYear(
    val year: Int,
    val survivalRatePercent: Double,
    val lowerAssetWon: Long,
    val medianAssetWon: Long,
    val upperAssetWon: Long
)

data class RetirementSuccessResult(
    val successRatePercent: Double,
    val successfulPaths: Int,
    val simulations: Int,
    val medianFinalAssetWon: Long,
    val lowerFinalAssetWon: Long,
    val upperFinalAssetWon: Long,
    val medianDepletionYear: Int?,
    val rows: List<RetirementSuccessYear>
)

data class RetirementScenarioSuccessAnalysis(
    val scenarioId: String,
    val scenarioName: String,
    val type: ScenarioComparisonType,
    val inferredInitialAssetWon: Long,
    val expectedAnnualReturnPercent: Double,
    val historicalVolatility: HistoricalVolatilityEstimate,
    val result: RetirementSuccessResult
)

object RetirementSuccessEngine {
    fun calculate(rawInput: RetirementSuccessInput): RetirementSuccessResult {
        val input = rawInput.copy(
            initialAssetWon = rawInput.initialAssetWon.coerceAtLeast(0L),
            monthlySpendingWon = rawInput.monthlySpendingWon.coerceAtLeast(0L),
            expectedAnnualReturnPercent = rawInput.expectedAnnualReturnPercent.coerceIn(-100.0, 100.0),
            annualVolatilityPercent = rawInput.annualVolatilityPercent.coerceIn(0.0, 100.0),
            annualInflationPercent = rawInput.annualInflationPercent.coerceIn(-20.0, 30.0),
            years = rawInput.years.coerceIn(1, 60),
            simulations = rawInput.simulations.coerceIn(100, 20_000)
        )
        val random = Random(input.seed)
        val assetsByYear = Array(input.years) { DoubleArray(input.simulations) }
        val depletionYears = mutableListOf<Int>()
        var successfulPaths = 0

        repeat(input.simulations) { simulationIndex ->
            var asset = input.initialAssetWon.toDouble()
            var annualSpending = input.monthlySpendingWon * 12.0
            var depletionYear: Int? = if (asset <= 0.0) 1 else null

            repeat(input.years) { yearIndex ->
                if (asset > 0.0) {
                    val sampledReturn = input.expectedAnnualReturnPercent / 100.0 +
                        input.annualVolatilityPercent / 100.0 * nextGaussian(random)
                    asset = max(0.0, asset * (1.0 + max(sampledReturn, -1.0)) - annualSpending)
                    if (asset <= 0.0 && depletionYear == null) depletionYear = yearIndex + 1
                }
                assetsByYear[yearIndex][simulationIndex] = asset
                annualSpending *= 1.0 + input.annualInflationPercent / 100.0
            }

            if (depletionYear == null) successfulPaths++ else depletionYears += depletionYear!!
        }

        val rows = assetsByYear.mapIndexed { index, values ->
            values.sort()
            RetirementSuccessYear(
                year = index + 1,
                survivalRatePercent = values.count { it > 0.0 } * 100.0 / input.simulations,
                lowerAssetWon = percentile(values, 0.10),
                medianAssetWon = percentile(values, 0.50),
                upperAssetWon = percentile(values, 0.90)
            )
        }
        val finalRow = rows.last()
        depletionYears.sort()

        return RetirementSuccessResult(
            successRatePercent = successfulPaths * 100.0 / input.simulations,
            successfulPaths = successfulPaths,
            simulations = input.simulations,
            medianFinalAssetWon = finalRow.medianAssetWon,
            lowerFinalAssetWon = finalRow.lowerAssetWon,
            upperFinalAssetWon = finalRow.upperAssetWon,
            medianDepletionYear = depletionYears.takeIf { it.isNotEmpty() }?.let { it[it.lastIndex / 2] },
            rows = rows
        )
    }

    private fun nextGaussian(random: Random): Double {
        val first = max(random.nextDouble(), 1e-12)
        val second = random.nextDouble()
        return sqrt(-2.0 * ln(first)) * cos(2.0 * Math.PI * second)
    }

    private fun percentile(sortedValues: DoubleArray, ratio: Double): Long {
        if (sortedValues.isEmpty()) return 0L
        val position = ratio.coerceIn(0.0, 1.0) * sortedValues.lastIndex
        val lowerIndex = position.toInt()
        val upperIndex = min(lowerIndex + 1, sortedValues.lastIndex)
        val fraction = position - lowerIndex
        return (sortedValues[lowerIndex] * (1.0 - fraction) + sortedValues[upperIndex] * fraction)
            .roundToLong()
            .coerceAtLeast(0L)
    }
}

object RetirementScenarioSuccessEngine {
    fun calculate(
        series: ScenarioComparisonSeries,
        historicalVolatility: HistoricalVolatilityEstimate,
        simulations: Int = 5_000
    ): RetirementScenarioSuccessAnalysis? {
        val points = series.points.sortedBy { it.year }.filter { it.totalAssetWon >= 0L }
        if (points.isEmpty()) return null
        val runCount = simulations.coerceIn(100, 20_000)
        val volatility = historicalVolatility.annualizedVolatilityPercent.coerceIn(0.0, 100.0) / 100.0
        val observedReturns = points.zipWithNext().mapNotNull { (previous, current) ->
            if (previous.totalAssetWon <= 0L) null else {
                ((current.totalAssetWon + current.monthlyCashFlowWon * 12.0) /
                    previous.totalAssetWon.toDouble() - 1.0).coerceIn(-0.8, 1.0)
            }
        }
        val averageReturn = observedReturns.average().takeIf { it.isFinite() } ?: 0.0
        val baselineReturns = points.indices.map { index ->
            if (index == 0) averageReturn else observedReturns.getOrElse(index - 1) { averageReturn }
        }
        val firstCashFlow = points.first().monthlyCashFlowWon * 12.0
        val inferredInitialAsset = ((points.first().totalAssetWon + firstCashFlow) /
            (1.0 + baselineReturns.first()).coerceAtLeast(0.2)).coerceAtLeast(0.0)
        val random = Random(20_260_714L + series.id.hashCode().toLong())
        val assetsByYear = Array(points.size) { DoubleArray(runCount) }
        val depletionYears = mutableListOf<Int>()
        var successfulPaths = 0

        repeat(runCount) { simulationIndex ->
            var asset = inferredInitialAsset
            var depletionYear: Int? = if (asset <= 0.0) points.first().year else null
            points.forEachIndexed { index, point ->
                if (asset > 0.0) {
                    val sampledReturn = baselineReturns[index] + volatility * nextGaussian(random)
                    val annualCashFlow = point.monthlyCashFlowWon.coerceAtLeast(0L) * 12.0
                    asset = max(0.0, asset * (1.0 + max(sampledReturn, -1.0)) - annualCashFlow)
                    if (asset <= 0.0 && depletionYear == null) depletionYear = point.year
                }
                assetsByYear[index][simulationIndex] = asset
            }
            if (depletionYear == null) successfulPaths++ else depletionYears += depletionYear!!
        }

        val rows = assetsByYear.mapIndexed { index, values ->
            values.sort()
            RetirementSuccessYear(
                year = points[index].year,
                survivalRatePercent = values.count { it > 0.0 } * 100.0 / runCount,
                lowerAssetWon = percentile(values, 0.10),
                medianAssetWon = percentile(values, 0.50),
                upperAssetWon = percentile(values, 0.90)
            )
        }
        val finalRow = rows.last()
        depletionYears.sort()
        val result = RetirementSuccessResult(
            successRatePercent = successfulPaths * 100.0 / runCount,
            successfulPaths = successfulPaths,
            simulations = runCount,
            medianFinalAssetWon = finalRow.medianAssetWon,
            lowerFinalAssetWon = finalRow.lowerAssetWon,
            upperFinalAssetWon = finalRow.upperAssetWon,
            medianDepletionYear = depletionYears.takeIf { it.isNotEmpty() }?.let { it[it.lastIndex / 2] },
            rows = rows
        )
        return RetirementScenarioSuccessAnalysis(
            scenarioId = series.id,
            scenarioName = series.name,
            type = series.type,
            inferredInitialAssetWon = inferredInitialAsset.roundToLong(),
            expectedAnnualReturnPercent = averageReturn * 100.0,
            historicalVolatility = historicalVolatility,
            result = result
        )
    }

    private fun nextGaussian(random: Random): Double {
        val first = max(random.nextDouble(), 1e-12)
        val second = random.nextDouble()
        return sqrt(-2.0 * ln(first)) * cos(2.0 * Math.PI * second)
    }

    private fun percentile(sortedValues: DoubleArray, ratio: Double): Long {
        if (sortedValues.isEmpty()) return 0L
        val position = ratio.coerceIn(0.0, 1.0) * sortedValues.lastIndex
        val lowerIndex = position.toInt()
        val upperIndex = min(lowerIndex + 1, sortedValues.lastIndex)
        val fraction = position - lowerIndex
        return (sortedValues[lowerIndex] * (1.0 - fraction) + sortedValues[upperIndex] * fraction)
            .roundToLong()
            .coerceAtLeast(0L)
    }
}
