package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

class SelfDividendEngineTest {
    @Test
    fun calculate_keepsAssetsWithoutOwnWithdrawalInCombinedPortfolio() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.0,
                    investmentAmount = 900_000_000.0,
                    baseAnnualWithdrawal = 0.0,
                    withdrawalGrowthRate = 0.0
                ),
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.0,
                    investmentAmount = 100_000_000.0,
                    baseAnnualWithdrawal = 10_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 0
        ).single()

        assertEquals(990_000_000L, firstYear.totalAsset)
        assertEquals(10_000_000L, firstYear.grossSale)
    }

    @Test
    fun calculate_deductsAnnualTaxFromTheMonthlyAfterTaxAmount() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = true,
                    expectedAnnualReturn = 0.30,
                    investmentAmount = 1_000_000_000.0,
                    baseAnnualWithdrawal = 50_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 0
        ).single()

        assertEquals(50_000_000L, firstYear.grossSale)
        assertTrue(firstYear.capitalGainsTax > 0L)
        assertTrue(abs(firstYear.annualTakeHome - (firstYear.grossSale - firstYear.capitalGainsTax)) <= 1L)
        assertTrue(
            abs(
                firstYear.monthlyTakeHome -
                    ((firstYear.grossSale - firstYear.capitalGainsTax) / 12.0).roundToLong()
            ) <= 1L
        )
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
            years = 0
        ).single()

        assertEquals(0L, firstYear.capitalGainsTax)
        assertEquals(10_000_000L, firstYear.grossSale)
        assertTrue(firstYear.realizedGain in 1L..2_500_000L)
    }

    @Test
    fun calculate_labelsTheFirstTwelveMonthsAsYearZero() {
        val rows = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.0,
                    investmentAmount = 1_200_000_000.0,
                    baseAnnualWithdrawal = 48_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 1
        )

        assertEquals(listOf(0, 1), rows.map { it.year })
        assertEquals(4_000_000L, rows.first().monthlyTakeHome)
        assertEquals(48_000_000L, rows.first().grossSale)
        assertEquals(1_152_000_000L, rows.first().totalAsset)
    }

    @Test
    fun calculate_compoundsReturnAndWithdrawsEveryMonth() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.12,
                    investmentAmount = 1_200_000_000.0,
                    baseAnnualWithdrawal = 48_000_000.0,
                    withdrawalGrowthRate = 0.0
                )
            ),
            years = 0
        ).single()
        val monthlyReturn = 1.12.pow(1.0 / 12.0) - 1.0
        var expectedAsset = 1_200_000_000.0
        repeat(12) {
            expectedAsset = expectedAsset * (1.0 + monthlyReturn) - 4_000_000.0
        }

        assertEquals(expectedAsset.roundToLong(), firstYear.totalAsset)
        assertEquals(48_000_000L, firstYear.grossSale)
    }

    @Test
    fun calculate_usesAfterTaxDividendBeforeSellingForTheWithdrawalTarget() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.0,
                    investmentAmount = 1_200_000_000.0,
                    baseAnnualWithdrawal = 48_000_000.0,
                    withdrawalGrowthRate = 0.0,
                    annualDividendYield = 0.015,
                    dividendWithholdingTaxRate = 0.15
                )
            ),
            years = 0
        ).single()

        assertEquals(18_000_000L, firstYear.grossDividend)
        assertEquals(2_700_000L, firstYear.dividendTax)
        assertEquals(15_300_000L, firstYear.afterTaxDividend)
        assertEquals(32_700_000L, firstYear.grossSale)
        assertEquals(48_000_000L, firstYear.annualTakeHome)
        assertEquals(4_000_000L, firstYear.monthlyTakeHome)
    }

    @Test
    fun calculate_dividendTaxIncludesWithholdingHealthInsuranceAndComprehensiveTax() {
        val firstYear = SelfDividendEngine.calculate(
            assets = listOf(
                SelfDividendAssetInput(
                    taxable = false,
                    expectedAnnualReturn = 0.0,
                    investmentAmount = 1_200_000_000.0,
                    baseAnnualWithdrawal = 60_000_000.0,
                    withdrawalGrowthRate = 0.0,
                    annualDividendYield = 0.04,
                    dividendWithholdingTaxRate = 0.15
                )
            ),
            years = 0
        ).single()

        assertEquals(48_000_000L, firstYear.grossDividend)
        assertEquals(12_952_704L, firstYear.dividendTax)
        assertEquals(35_047_296L, firstYear.afterTaxDividend)
        assertEquals(24_952_704L, firstYear.grossSale)
        assertEquals(60_000_000L, firstYear.annualTakeHome)
    }
}
