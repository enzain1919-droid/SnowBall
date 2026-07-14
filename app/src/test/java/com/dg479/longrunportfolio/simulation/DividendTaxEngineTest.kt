package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Test

class DividendTaxEngineTest {
    @Test
    fun afterTaxAnnual_appliesWithholdingOnlyAtThreshold() {
        val net = DividendTaxEngine.afterTaxAnnualWon(
            grossAnnualDividendWon = 20_000_000.0,
            person = DividendTaxPerson(),
            withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
        )

        assertEquals(17_000_000.0, net, 0.01)
    }

    @Test
    fun afterTaxAnnual_appliesComprehensiveTaxAndHealthInsuranceAboveThreshold() {
        val net = DividendTaxEngine.afterTaxAnnualWon(
            grossAnnualDividendWon = 30_000_000.0,
            person = DividendTaxPerson(),
            withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
        )

        assertEquals(22_399_560.0, net, 0.01)
    }

    @Test
    fun afterTaxAnnual_splitsIncomeAcrossPeople() {
        val net = DividendTaxEngine.afterTaxAnnualWon(
            grossAnnualDividendWon = 30_000_000.0,
            people = listOf(DividendTaxPerson(), DividendTaxPerson()),
            withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
        )

        assertEquals(25_500_000.0, net, 0.01)
    }

    @Test
    fun requiredShares_findsMinimumSharesForAfterTaxTarget() {
        val shares = DividendTaxEngine.requiredShares(
            grossAnnualDividendPerShare = 200_000.0,
            targetAnnualDividendWon = 1_700_000L,
            people = listOf(DividendTaxPerson()),
            withholdingTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
        )

        assertEquals(10L, shares)
    }
}
