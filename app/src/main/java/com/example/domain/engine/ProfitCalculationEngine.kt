package com.example.domain.engine

import com.example.domain.model.LoadItem
import com.example.domain.model.MetalGrade

data class ProfitCalculationResult(
    val grossPayout: Double,
    val netProfit: Double,
    val totalExpenses: Double,
    val valueLossWarning: Double, // Amount lost by selling dirty/unseparated
    val profitBreakdown: Map<MetalGrade, Double> // Percentage of profit contributed by each metal (0.0 to 1.0)
)

class ProfitCalculationEngine {

    /**
     * Dirty metal often gets downgraded to a lower tier or a flat percentage deduction.
     * For this engine, we assume a standard 30% price penalty for dirty/unseparated non-ferrous metals,
     * except steel which usually has a smaller penalty or is bought as-is.
     */
    private fun getDirtyPriceMultiplier(grade: MetalGrade): Double {
        return if (grade == MetalGrade.HEAVY_MELTING_STEEL) {
            0.85 // 15% penalty
        } else {
            0.70 // 30% penalty for dirty copper/brass/aluminum
        }
    }

    fun calculateProfit(load: LoadItem): ProfitCalculationResult {
        var grossPayout = 0.0
        var maxPotentialPayout = 0.0
        val revenueByGrade = mutableMapOf<MetalGrade, Double>()

        for (item in load.items) {
            val effectivePrice = if (item.isCleaned) {
                item.pricePerLb
            } else {
                item.pricePerLb * getDirtyPriceMultiplier(item.grade)
            }

            val safeWeight = item.weightLbs.coerceAtLeast(0.0)
            val safePrice = item.pricePerLb.coerceAtLeast(0.0)
            val yieldMultiplier = item.recoverableYieldPercent.coerceIn(0.0, 100.0) / 100.0
            val itemRevenue = (if (item.isCleaned) safePrice else safePrice * getDirtyPriceMultiplier(item.grade)) * safeWeight * yieldMultiplier
            grossPayout += itemRevenue
            maxPotentialPayout += safePrice * safeWeight
            
            // Track revenue per grade for the breakdown
            revenueByGrade[item.grade] = (revenueByGrade[item.grade] ?: 0.0) + itemRevenue
        }

        val totalExpenses = load.expense.totalExpense
        val netProfit = grossPayout - totalExpenses
        val valueLossWarning = maxPotentialPayout - grossPayout

        val profitBreakdown = mutableMapOf<MetalGrade, Double>()
        if (grossPayout > 0) {
            for ((grade, revenue) in revenueByGrade) {
                profitBreakdown[grade] = revenue / grossPayout
            }
        }

        return ProfitCalculationResult(
            grossPayout = grossPayout,
            netProfit = netProfit,
            totalExpenses = totalExpenses,
            valueLossWarning = if (valueLossWarning > 0) valueLossWarning else 0.0,
            profitBreakdown = profitBreakdown
        )
    }
}
