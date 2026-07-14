package com.dg479.longrunportfolio.simulation

data class DividendTaxPerson(
    val comprehensiveExtraTaxRatePercent: Double = 0.0
)

object DividendTaxEngine {
    const val DOMESTIC_WITHHOLDING_TAX_RATE = 0.154
    const val OVERSEAS_WITHHOLDING_TAX_RATE = 0.15
    const val DEFAULT_COMPREHENSIVE_EXTRA_TAX_RATE_PERCENT = 6.6

    private const val ComprehensiveTaxThresholdWon = 20_000_000.0
    private const val HealthInsuranceIncomeThresholdWon = 20_000_000.0
    private const val HealthInsuranceRate = 0.0719
    private const val LongTermCareRate = 0.009448
    private const val MaxShareSearch = 10_000_000_000L

    fun estimatedHealthInsuranceWon(grossAnnualDividendWon: Double): Double {
        val gross = grossAnnualDividendWon.coerceAtLeast(0.0)
        if (gross <= HealthInsuranceIncomeThresholdWon) return 0.0
        return gross * (HealthInsuranceRate + LongTermCareRate)
    }

    fun estimatedHealthInsuranceWon(
        grossAnnualDividendWon: Double,
        people: List<DividendTaxPerson>
    ): Double {
        val gross = grossAnnualDividendWon.coerceAtLeast(0.0)
        if (gross <= 0.0) return 0.0
        val activePeople = people.ifEmpty { listOf(DividendTaxPerson()) }
        val perPersonGross = gross / activePeople.size
        return activePeople.sumOf { estimatedHealthInsuranceWon(perPersonGross) }
    }

    fun afterTaxAnnualWon(
        grossAnnualDividendWon: Double,
        person: DividendTaxPerson,
        withholdingTaxRate: Double = DOMESTIC_WITHHOLDING_TAX_RATE
    ): Double {
        val gross = grossAnnualDividendWon.coerceAtLeast(0.0)
        val withholdingTax = gross * withholdingTaxRate.coerceAtLeast(0.0)
        val comprehensiveTaxBase = (gross - ComprehensiveTaxThresholdWon).coerceAtLeast(0.0)
        val extraTaxRate = person.comprehensiveExtraTaxRatePercent
            .takeIf { it > 0.0 }
            ?: DEFAULT_COMPREHENSIVE_EXTRA_TAX_RATE_PERCENT
        val extraIncomeTax = comprehensiveTaxBase * (extraTaxRate.coerceAtLeast(0.0) / 100.0)
        val healthInsurance = estimatedHealthInsuranceWon(gross)
        return (gross - withholdingTax - extraIncomeTax - healthInsurance).coerceAtLeast(0.0)
    }

    fun afterTaxAnnualWon(
        grossAnnualDividendWon: Double,
        people: List<DividendTaxPerson>,
        withholdingTaxRate: Double = DOMESTIC_WITHHOLDING_TAX_RATE
    ): Double {
        val gross = grossAnnualDividendWon.coerceAtLeast(0.0)
        if (gross <= 0.0) return 0.0
        val activePeople = people.ifEmpty { listOf(DividendTaxPerson()) }
        val perPersonGross = gross / activePeople.size
        return activePeople.sumOf { person ->
            afterTaxAnnualWon(perPersonGross, person, withholdingTaxRate)
        }
    }

    fun requiredShares(
        grossAnnualDividendPerShare: Double,
        targetAnnualDividendWon: Long,
        people: List<DividendTaxPerson>,
        withholdingTaxRate: Double = DOMESTIC_WITHHOLDING_TAX_RATE
    ): Long {
        if (grossAnnualDividendPerShare <= 0.0 || targetAnnualDividendWon <= 0L) return 0L

        fun annualDividendFor(shares: Long): Double = afterTaxAnnualWon(
            grossAnnualDividendWon = shares * grossAnnualDividendPerShare,
            people = people,
            withholdingTaxRate = withholdingTaxRate
        )

        var high = 1L
        while (annualDividendFor(high) < targetAnnualDividendWon && high < MaxShareSearch) {
            high = (high * 2).coerceAtMost(MaxShareSearch)
        }
        var low = 0L
        while (low < high) {
            val mid = low + (high - low) / 2
            if (annualDividendFor(mid) >= targetAnnualDividendWon) high = mid else low = mid + 1
        }
        return low
    }
}
