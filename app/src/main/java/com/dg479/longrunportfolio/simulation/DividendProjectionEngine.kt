package com.dg479.longrunportfolio.simulation

import kotlin.math.pow
import kotlin.math.roundToLong

data class DividendProjectionInput(
    val grossAnnualDividendWon: Double,
    val initialAssetWon: Long,
    val dividendGrowthRatePercent: Double,
    val priceGrowthRatePercent: Double,
    val people: List<DividendTaxPerson>,
    val withholdingTaxRate: Double
)

data class DividendProjectionRow(
    val year: Int,
    val monthlyDividend: Long,
    val totalAsset: Long
)

object DividendProjectionEngine {
    fun calculate(
        input: DividendProjectionInput,
        years: Int = 20
    ): List<DividendProjectionRow> {
        if (input.grossAnnualDividendWon <= 0.0 || input.initialAssetWon <= 0L || years <= 0) {
            return emptyList()
        }

        val annualDividendGrowth = 1.0 + input.dividendGrowthRatePercent / 100.0
        val annualPriceGrowth = 1.0 + input.priceGrowthRatePercent / 100.0
        return (1..years).map { year ->
            val grossAnnualDividend = input.grossAnnualDividendWon *
                annualDividendGrowth.pow(year.toDouble())
            val annualAfterTaxDividend = DividendTaxEngine.afterTaxAnnualWon(
                grossAnnualDividendWon = grossAnnualDividend,
                people = input.people,
                withholdingTaxRate = input.withholdingTaxRate
            )
            DividendProjectionRow(
                year = year,
                monthlyDividend = (annualAfterTaxDividend / 12.0).roundToLong().coerceAtLeast(0L),
                totalAsset = (input.initialAssetWon * annualPriceGrowth.pow(year.toDouble()))
                    .roundToLong()
                    .coerceAtLeast(0L)
            )
        }
    }
}
