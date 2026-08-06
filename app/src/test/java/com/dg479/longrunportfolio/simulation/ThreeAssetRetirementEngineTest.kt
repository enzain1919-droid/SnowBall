package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThreeAssetRetirementEngineTest {
    @Test
    fun calculate_accumulatesDividendSurplusAsCash() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 1.0, jepq = 0.0, gpiq = 0.0, voo = 0.0, qld = 0.0),
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
                allocation = ThreeAssetAllocation(schd = 0.5, jepq = 0.3, gpiq = 0.0, voo = 0.0, qld = 0.2),
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
                allocation = ThreeAssetAllocation(schd = 1.0, jepq = 0.0, gpiq = 0.0, voo = 0.0, qld = 0.0),
                monthlyExpenseWon = 200_000_000L
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(2_400_000_000L, firstYear.annualExpenseWon)
        assertEquals(1_000_000_000L, firstYear.actualAnnualCashFlowWon)
        assertEquals(0L, firstYear.totalAssetWon)
    }

    @Test
    fun calculate_includesJepqDividendAndAssetValue() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 0.0, jepq = 1.0, gpiq = 0.0, voo = 0.0, qld = 0.0),
                monthlyExpenseWon = 0L,
                jepqYield = 0.08
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(80_000_000L, firstYear.grossAnnualDividendWon)
        assertEquals(1_080_000_000L, firstYear.totalAssetWon)
        assertEquals(1_000_000_000L, firstYear.jepqAssetWon)
    }

    @Test
    fun calculate_includesGpiqDividendAndAssetValue() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 0.0, jepq = 0.0, gpiq = 1.0, voo = 0.0, qld = 0.0),
                monthlyExpenseWon = 0L,
                gpiqYield = 0.09
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(90_000_000L, firstYear.grossAnnualDividendWon)
        assertEquals(1_090_000_000L, firstYear.totalAssetWon)
        assertEquals(1_000_000_000L, firstYear.gpiqAssetWon)
        assertEquals(1_000_000_000.0, result.initialGpiqShares, 0.0)
    }

    @Test
    fun calculate_supportsArbitraryDynamicAssets() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 0.0, jepq = 0.0, gpiq = 0.0, voo = 0.0, qld = 0.0),
                monthlyExpenseWon = 0L
            ).copy(
                assets = listOf(
                    ThreeAssetPositionInput(
                        ticker = "QQQI",
                        name = "NEOS Nasdaq-100 High Income ETF",
                        allocation = 1.0,
                        price = 1.0,
                        dividendYield = 0.10,
                        dividendGrowth = 0.0,
                        priceGrowth = 0.0
                    )
                )
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(100_000_000L, firstYear.grossAnnualDividendWon)
        assertEquals(1_000_000_000L, firstYear.assetValuesWon["QQQI"] ?: -1L)
        assertEquals(1_100_000_000L, firstYear.totalAssetWon)
        assertEquals(1_000_000_000.0, result.initialSharesByTicker["QQQI"] ?: -1.0, 0.0)
    }

    @Test
    fun calculate_appliesOverseasDividendTaxSeparatelyFromOtherDeductions() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 1.0, jepq = 0.0, gpiq = 0.0, voo = 0.0, qld = 0.0),
                monthlyExpenseWon = 0L,
                schdYield = 0.10,
                overseasDividendTaxRate = 0.15,
                taxAndInsuranceRate = 0.234
            ),
            years = 1
        )

        val firstYear = result.rows.single()
        assertEquals(100_000_000L, firstYear.grossAnnualDividendWon)
        assertEquals(61_600_000L, firstYear.netAnnualDividendWon)
    }

    @Test
    fun calculate_includesVooAssetValueAndShares() {
        val result = ThreeAssetRetirementEngine.calculate(
            input = baseInput(
                allocation = ThreeAssetAllocation(schd = 0.0, jepq = 0.0, gpiq = 0.0, voo = 1.0, qld = 0.0),
                monthlyExpenseWon = 0L
            ),
            years = 1
        )

        assertEquals(1_000_000_000.0, result.initialVooShares, 0.0)
        assertEquals(1_000_000_000L, result.rows.single().vooAssetWon)
        assertEquals(1_000_000_000L, result.finalAssetWon)
    }

    private fun baseInput(
        allocation: ThreeAssetAllocation,
        monthlyExpenseWon: Long,
        schdYield: Double = 0.0,
        jepqYield: Double = 0.0,
        gpiqYield: Double = 0.0,
        stressTestEnabled: Boolean = false,
        overseasDividendTaxRate: Double = 0.0,
        taxAndInsuranceRate: Double = 0.0
    ) = ThreeAssetRetirementInput(
        totalCapitalWon = 1_000_000_000L,
        monthlyExpenseWon = monthlyExpenseWon,
        allocation = allocation,
        exchangeRate = 1.0,
        schdPrice = 1.0,
        jepqPrice = 1.0,
        gpiqPrice = 1.0,
        vooPrice = 1.0,
        qldPrice = 1.0,
        schdYield = schdYield,
        schdDividendGrowth = 0.0,
        schdPriceGrowth = 0.0,
        jepqYield = jepqYield,
        jepqDividendGrowth = 0.0,
        jepqPriceGrowth = 0.0,
        gpiqYield = gpiqYield,
        gpiqDividendGrowth = 0.0,
        gpiqPriceGrowth = 0.0,
        vooPriceGrowth = 0.0,
        qldPriceGrowth = 0.0,
        cashYield = 0.0,
        inflationRate = 0.0,
        overseasDividendTaxRate = overseasDividendTaxRate,
        taxAndInsuranceRate = taxAndInsuranceRate,
        stressTestEnabled = stressTestEnabled
    )
}
