package com.dg479.longrunportfolio.formatting

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object CurrencyDisplayFormatter {
    fun format(valueWon: Long, usdKrw: Double, displayUsd: Boolean): String {
        if (!displayUsd) {
            return "${NumberFormat.getNumberInstance(Locale.KOREA).format(valueWon)}원"
        }
        val dollars = valueWon / usdKrw.coerceAtLeast(1.0)
        return "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }.format(dollars)}"
    }

    fun formatSigned(valueWon: Long, usdKrw: Double, displayUsd: Boolean): String =
        if (valueWon >= 0L) {
            "+${format(valueWon, usdKrw, displayUsd)}"
        } else {
            "-${format(abs(valueWon), usdKrw, displayUsd)}"
        }

    fun formatCompact(valueWon: Long, usdKrw: Double, displayUsd: Boolean): String {
        if (!displayUsd) {
            return when {
                abs(valueWon) >= 100_000_000L -> "${decimal(valueWon / 100_000_000.0)}억"
                abs(valueWon) >= 10_000L -> "${decimal(valueWon / 10_000.0)}만"
                else -> format(valueWon, usdKrw, false)
            }
        }
        val dollars = valueWon / usdKrw.coerceAtLeast(1.0)
        return when {
            kotlin.math.abs(dollars) >= 1_000_000.0 -> "$${decimal(dollars / 1_000_000.0)}M"
            kotlin.math.abs(dollars) >= 1_000.0 -> "$${decimal(dollars / 1_000.0)}K"
            else -> "$${decimal(dollars)}"
        }
    }

    private fun decimal(value: Double): String =
        NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }.format(value)
}
