package com.dg479.longrunportfolio.protection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AppProtectionTest {
    @Test
    fun modesExposeRequestedWaitAndEntryLimits() {
        assertEquals(60L * 60L * 1_000L, ProtectionMode.WEAK.waitMillis)
        assertEquals(12, ProtectionMode.WEAK.dailyEntryLimit)
        assertEquals(4L * 60L * 60L * 1_000L, ProtectionMode.MEDIUM.waitMillis)
        assertEquals(4, ProtectionMode.MEDIUM.dailyEntryLimit)
        assertEquals(4L * 60L * 60L * 1_000L, ProtectionMode.STRONG.waitMillis)
        assertEquals(4, ProtectionMode.STRONG.dailyEntryLimit)
    }

    @Test
    fun qrVerificationUsesStoredHash() {
        val settings = ProtectionSettings(qrHash = hashQrContent("snowball-qr"))

        assertTrue(settings.matchesQr("snowball-qr"))
        assertFalse(settings.matchesQr("different-qr"))
        assertFalse(settings.matchesQr(null))
    }

    @Test
    fun monthlyReviewRemainsAvailableUntilCompleted() {
        val missedFirstDay = PortfolioReviewStatus(
            today = LocalDate.of(2026, 10, 7),
            completedMonth = "2026-09"
        )

        assertTrue(missedFirstDay.isReviewAvailable)
        assertEquals(LocalDate.of(2026, 10, 1), missedFirstDay.nextReviewDate)
    }

    @Test
    fun completedReviewMovesNextDateToFollowingMonth() {
        val completed = PortfolioReviewStatus(
            today = LocalDate.of(2026, 12, 20),
            completedMonth = "2026-12"
        )

        assertFalse(completed.isReviewAvailable)
        assertEquals(LocalDate.of(2027, 1, 1), completed.nextReviewDate)
    }
}
