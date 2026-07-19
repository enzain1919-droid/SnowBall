package com.dg479.longrunportfolio.planning

import java.time.LocalDate
import java.time.Period

object InvestmentDurationFormatter {
    fun format(startDate: LocalDate?, today: LocalDate = LocalDate.now()): String {
        if (startDate == null || today.isBefore(startDate)) return "시작 전"

        val elapsed = Period.between(startDate, today)
        val ordinalDay = elapsed.days + 1
        return when {
            elapsed.years > 0 -> "${elapsed.years}년 ${elapsed.months}개월 ${ordinalDay}일째"
            elapsed.months > 0 -> "${elapsed.months}개월 ${ordinalDay}일째"
            else -> "${ordinalDay}일째"
        }
    }
}
