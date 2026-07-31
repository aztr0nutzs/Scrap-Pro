package com.example.domain.engine

import com.example.domain.model.WireCategory

data class WireStrippingResult(
    val insulatedPayout: Double,
    val strippedPayout: Double,
    val netValueGain: Double,
    val effectiveHourlyWage: Double,
    val laborHours: Double,
    val machinePaybackLoads: Double?,
    val recommendation: Recommendation
) {
    enum class Recommendation {
        RECOMMENDED_TO_STRIP,
        SELL_INSULATED
    }
}

class WireStrippingRoiEngine {

    /**
     * Calculates the return on investment for stripping wire.
     * 
     * @param category The type of wire being stripped.
     * @param totalWeightLbs The total weight of the insulated wire.
     * @param insulatedPricePerLb The price per lb if sold as-is (insulated).
     * @param bareBrightPricePerLb The price per lb of the recovered bare bright copper.
     * @param strippingSpeedLbsPerHour The estimated speed of stripping this wire.
     */
    fun calculateRoi(
        category: WireCategory,
        totalWeightLbs: Double,
        insulatedPricePerLb: Double,
        bareBrightPricePerLb: Double,
        strippingSpeedLbsPerHour: Double,
        recoveryRatePercent: Double = category.recoveryYieldPercentage * 100.0,
        machineCost: Double = 0.0
    ): WireStrippingResult {
        if (totalWeightLbs <= 0 || strippingSpeedLbsPerHour <= 0) {
            return WireStrippingResult(0.0, 0.0, 0.0, 0.0, 0.0, null, WireStrippingResult.Recommendation.SELL_INSULATED)
        }

        // Payout if sold as-is
        val safeInsulatedPrice = insulatedPricePerLb.coerceAtLeast(0.0)
        val safeBarePrice = bareBrightPricePerLb.coerceAtLeast(0.0)
        val insulatedPayout = totalWeightLbs * safeInsulatedPrice

        // Payout if stripped (Weight * Yield * Bare Bright Price)
        val strippedWeight = totalWeightLbs * (recoveryRatePercent.coerceIn(0.0, 100.0) / 100.0)
        val strippedPayout = strippedWeight * safeBarePrice

        // How much more money is made by stripping
        val netValueGain = strippedPayout - insulatedPayout

        // How long it will take to strip the wire
        val hoursRequired = totalWeightLbs / strippingSpeedLbsPerHour

        // Effective hourly wage for the time spent stripping
        val effectiveHourlyWage = if (hoursRequired > 0) netValueGain / hoursRequired else 0.0
        val machinePaybackLoads = if (machineCost > 0.0 && netValueGain > 0.0) machineCost.coerceAtLeast(0.0) / netValueGain else null

        // Recommend stripping if the effective wage is $25/hr or more
        val recommendation = if (effectiveHourlyWage >= 25.0) {
            WireStrippingResult.Recommendation.RECOMMENDED_TO_STRIP
        } else {
            WireStrippingResult.Recommendation.SELL_INSULATED
        }

        return WireStrippingResult(
            insulatedPayout = insulatedPayout,
            strippedPayout = strippedPayout,
            netValueGain = netValueGain,
            effectiveHourlyWage = effectiveHourlyWage,
            laborHours = hoursRequired,
            machinePaybackLoads = machinePaybackLoads,
            recommendation = recommendation
        )
    }
}
