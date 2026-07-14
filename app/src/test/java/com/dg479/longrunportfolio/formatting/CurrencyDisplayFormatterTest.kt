package com.dg479.longrunportfolio.formatting

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyDisplayFormatterTest {
    @Test
    fun formatKeepsWonWhenKrwIsSelected() {
        assertEquals("1,535,290원", CurrencyDisplayFormatter.format(1_535_290L, 1_535.29, false))
    }

    @Test
    fun formatConvertsWonToDollarsAtCurrentRate() {
        assertEquals("$1,000", CurrencyDisplayFormatter.format(1_535_290L, 1_535.29, true))
        assertEquals("-$1,000", CurrencyDisplayFormatter.formatSigned(-1_535_290L, 1_535.29, true))
    }

    @Test
    fun compactDollarValuesFitChartsAndTables() {
        assertEquals("$1M", CurrencyDisplayFormatter.formatCompact(1_535_290_000L, 1_535.29, true))
        assertEquals("$10K", CurrencyDisplayFormatter.formatCompact(15_352_900L, 1_535.29, true))
    }
}
