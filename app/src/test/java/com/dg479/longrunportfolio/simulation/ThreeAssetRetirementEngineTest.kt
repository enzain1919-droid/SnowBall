package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThreeAssetRetirementEngineTest {
    @Test
    fun calculate_accumulatesDividendSurplusAsCash() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 1.0, jepq = 0.0, qld = 0.0),
                monthlyExpenseWon = 5_000_000L,
                schdYield = 0.10
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(100_000_000L, firstYear.grossAnnualDividendWon)
        assertEquals(1_040_000_000L, firstYear.totalAssetWon)
        assertNull(result.cashDepletedYear)
    }

    @Test
    fun calculate_appliesFirstYearStressPrices() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 0.5, jepq = 0.3, qld = 0.2),
                monthlyExpenseWon = 0L,
                stressTestEnabled = true
            ),
            years = 1
        )

        assertEquals(640_000_000L, result.rows.single().totalAssetWon)
    }

    @Test
    fun calculate_reportsOnlyCashFlowThatAssetsCanFund() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 1.0, jepq = 0.0, qld = 0.0),
                monthlyExpenseWon = 200_000_000L
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(2_400_000_000L, firstYear.annualExpenseWon)
        assertEquals(1_000_000_000L, firstYear.actualAnnualCashFlowWon)
        assertEquals(0L, firstYear.totalAssetWon)
    }

    private fun baseInput(
        allocation: ThreeAssetAllocation,
        monthlyExpenseWon: Long,
        schdYield: Double = 0.0,
        stressTestEnabled: Boolean = false
    ) = ThreeAssetRetirementInput(
        totalCapitalWon = 1_000_000_000L,
        monthlyExpenseWon = monthlyExpenseWon,
        allocation = allocation,
        exchangeRate = 1.0,
        schdPrice = 1.0,
        jepqPrice = 1.0,
        qldPrice = 1.0,
        schdYield = schdYield,
        schdDividendGrowth = 0.0,
        schdPriceGrowth = 0.0,
        jepqYield = 0.0,
        jepqDividendGrowth = 0.0,
        jepqPriceGrowth = 0.0,
        qldPriceGrowth = 0.0,
        cashYield = 0.0,
        inflationRate = 0.0,
        taxAndInsuranceRate = 0.0,
        stressTestEnabled = stressTestEnabled
    )
}
