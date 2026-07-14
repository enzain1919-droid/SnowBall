package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SelfDividendEngineTest {
    @Test
    fun calculate_solvesGrossSaleForRequestedAfterTaxWithdrawal() {
        val rows = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = true,
                    expectedAnnualReturn = 0.10,
                    investmentAmount = 1_000_000_000.0,
                    baseAnnualWithdrawal = 50_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 1
        )

        val firstYear = rows.single()
        assertEquals(50_000_000L, firstYear.annualTakeHome)
        assertEquals(50_459_184L, firstYear.grossSale)
        assertEquals(459_184L, firstYear.capitalGainsTax)
        assertEquals(1_049_540_816L, firstYear.totalAsset)
    }

    @Test
    fun calculate_appliesAnnualDeductionBeforeCapitalGainsTax() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = true,
                    expectedAnnualReturn = 0.01,
                    investmentAmount = 1_000_000_000.0,
                    baseAnnualWithdrawal = 10_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 1
        ).single()

        assertEquals(0L, firstYear.capitalGainsTax)
        assertEquals(10_000_000L, firstYear.grossSale)
        assertTrue(firstYear.realizedGain in 1L..2_500_000L)
    }
}
