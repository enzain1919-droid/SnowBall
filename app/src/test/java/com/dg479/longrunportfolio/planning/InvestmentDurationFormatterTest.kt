package com.dg479.longrunportfolio.planning

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class InvestmentDurationFormatterTest {
    @Test
    fun sameDayIsFirstDay() {
        val date = LocalDate.of(2026, 7, 19)

        assertEquals("1일째", InvestmentDurationFormatter.format(date, date))
    }

    @Test
    fun lessThanMonthShowsDaysOnly() {
        assertEquals(
            "22일째",
            InvestmentDurationFormatter.format(
                LocalDate.of(2026, 6, 28),
                LocalDate.of(2026, 7, 19)
            )
        )
    }

    @Test
    fun lessThanYearShowsMonthsAndDays() {
        assertEquals(
            "5개월 4일째",
            InvestmentDurationFormatter.format(
                LocalDate.of(2026, 1, 16),
                LocalDate.of(2026, 6, 19)
            )
        )
    }

    @Test
    fun oneYearOrMoreShowsYearsMonthsAndDays() {
        assertEquals(
            "2년 3개월 5일째",
            InvestmentDurationFormatter.format(
                LocalDate.of(2024, 4, 15),
                LocalDate.of(2026, 7, 19)
            )
        )
    }

    @Test
    fun futureDateShowsNotStarted() {
        assertEquals(
            "시작 전",
            InvestmentDurationFormatter.format(
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 7, 19)
            )
        )
    }
}
