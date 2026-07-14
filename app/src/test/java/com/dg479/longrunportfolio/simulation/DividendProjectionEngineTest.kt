package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DividendProjectionEngineTest {
    @Test
    fun calculate_appliesDividendAndPriceGrowthForEachYear() {
        val rows = DividendProjectionEngine.calculate(
            input = DividendProjectionInput(
                grossAnnualDividendWon = 12_000_000.0,
                initialAssetWon = 100_000_000L,
                dividendGrowthRatePercent = 10.0,
                priceGrowthRatePercent = 5.0,
                people = listOf(DividendTaxPerson()),
                withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
            ),
            years = 2
        )

        assertEquals(2, rows.size)
        assertEquals(935_000L, rows[0].monthlyDividend)
        assertEquals(105_000_000L, rows[0].totalAsset)
        assertEquals(1_028_500L, rows[1].monthlyDividend)
        assertEquals(110_250_000L, rows[1].totalAsset)
    }

    @Test
    fun calculate_reflectsTaxReductionFromMultiplePeople() {
        val onePerson = calculateFirstYear(people = listOf(DividendTaxPerson()))
        val twoPeople = calculateFirstYear(people = listOf(DividendTaxPerson(), DividendTaxPerson()))

        assertTrue(twoPeople.monthlyDividend > onePerson.monthlyDividend)
    }

    @Test
    fun calculate_returnsEmptyRowsForInvalidCapital() {
        val rows = DividendProjectionEngine.calculate(
            input = DividendProjectionInput(
                grossAnnualDividendWon = 12_000_000.0,
                initialAssetWon = 0L,
                dividendGrowthRatePercent = 0.0,
                priceGrowthRatePercent = 0.0,
                people = emptyList(),
                withholdingTaxRate = DividendTaxEngine.DOMESTIC_WITHHOLDING_TAX_RATE
            )
        )

        assertTrue(rows.isEmpty())
    }

    private fun calculateFirstYear(people: List<DividendTaxPerson>): DividendProjectionRow =
        DividendProjectionEngine.calculate(
            input = DividendProjectionInput(
                grossAnnualDividendWon = 30_000_000.0,
                initialAssetWon = 500_000_000L,
                dividendGrowthRatePercent = 0.0,
                priceGrowthRatePercent = 0.0,
                people = people,
                withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
            ),
            years = 1
        ).single()
}
