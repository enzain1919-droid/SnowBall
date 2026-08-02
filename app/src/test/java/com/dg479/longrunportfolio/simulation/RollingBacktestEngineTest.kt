package com.dg479.longrunportfolio.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RollingBacktestEngineTest {
    @Test
    fun `windows move one year at a time and end in current year`() {
        val windows = RollingBacktestEngine.windows(
            startYear = 2010,
            intervalYears = 10,
            currentYear = 2024
        )

        assertEquals(6, windows.size)
        assertEquals(RollingBacktestWindow(2010, 2019), windows.first())
        assertEquals(RollingBacktestWindow(2015, 2024), windows.last())
    }

    @Test
    fun `windows reject a period that cannot be completed`() {
        assertTrue(
            RollingBacktestEngine.windows(
                startYear = 2020,
                intervalYears = 10,
                currentYear = 2024
            ).isEmpty()
        )
        assertTrue(
            RollingBacktestEngine.windows(
                startYear = 2010,
                intervalYears = 0,
                currentYear = 2024
            ).isEmpty()
        )
    }

    @Test
    fun `monthly returns are compounded and annualized`() {
        val annualized = RollingBacktestEngine.annualizedReturnPercent(
            listOf(0.0) + List(12) { 0.01 }
        )

        assertEquals(12.6825, annualized, 0.0001)
    }

    @Test
    fun `investment period inflation is removed from the nominal final value`() {
        val adjusted = RollingBacktestEngine.inflationAdjustedValue(
            value = 1_379_000_000L,
            startYear = 2006,
            endYear = 2010,
            annualInflationRatesPercent = (2006..2010).associateWith { 3.0 }
        )

        assertEquals(1_189_537_514L, adjusted)
    }

    @Test
    fun `missing recent inflation years use the latest five year average`() {
        val adjusted = RollingBacktestEngine.inflationAdjustedValue(
            value = 1_000_000L,
            startYear = 2023,
            endYear = 2024,
            annualInflationRatesPercent = mapOf(
                2018 to 2.0,
                2019 to 2.0,
                2020 to 2.0,
                2021 to 2.0,
                2022 to 2.0
            )
        )

        assertEquals(961_169L, adjusted)
    }
}
