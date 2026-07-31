package com.example.domain.engine

import com.example.domain.model.CargoLoad
import com.example.domain.model.VehicleRating

data class PayloadSafetyResult(
    val truckPayloadPercentage: Double,
    val trailerPayloadPercentage: Double,
    val isTruckOverweight: Boolean,
    val isTrailerOverweight: Boolean,
    val loadedTrailerWeightLbs: Double,
    val tongueWeightPercentage: Double,
    val minimumSafeTongueWeightLbs: Double,
    val maximumSafeTongueWeightLbs: Double,
    val recommendations: List<String>
)

class PayloadSafetyEngine {

    /**
     * Evaluates the current load against vehicle ratings to ensure safety.
     */
    fun evaluatePayloadSafety(rating: VehicleRating, load: CargoLoad): PayloadSafetyResult {
        // Calculate percentages
        val truckPercentage = if (rating.truckMaxPayloadLbs > 0) {
            (load.truckBedCargoWeightLbs.coerceAtLeast(0.0) / rating.truckMaxPayloadLbs) * 100
        } else 0.0

        val trailerMaxPayload = rating.trailerMaxPayloadLbs
        val trailerPercentage = if (trailerMaxPayload > 0) {
            (load.trailerCargoWeightLbs.coerceAtLeast(0.0) / trailerMaxPayload) * 100
        } else 0.0

        val isTruckOverweight = truckPercentage > 100.0
        val isTrailerOverweight = trailerPercentage > 100.0

        val loadedTrailerWeight = (rating.trailerEmptyWeightLbs.coerceAtLeast(0.0) + load.trailerCargoWeightLbs.coerceAtLeast(0.0))
        val tonguePercentage = if (loadedTrailerWeight > 0.0) load.tongueWeightLbs.coerceAtLeast(0.0) / loadedTrailerWeight * 100.0 else 0.0
        val minimumTongue = loadedTrailerWeight * 0.10
        val maximumTongue = loadedTrailerWeight * 0.15
        val recommendations = mutableListOf<String>()

        if (isTruckOverweight) {
            recommendations.add("CRITICAL: Truck is overloaded! Move some cargo to the trailer if it has capacity, or remove weight.")
        } else if (truckPercentage > 80.0) {
            recommendations.add("Warning: Truck is nearing maximum payload. Ensure tires are properly inflated.")
        }

        if (isTrailerOverweight) {
            recommendations.add("CRITICAL: Trailer is overloaded! This is a major safety hazard. Remove weight immediately.")
        } else if (trailerPercentage > 80.0) {
            recommendations.add("Warning: Trailer is nearing maximum capacity.")
        }

        // Placement recommendations
        if (load.trailerCargoWeightLbs > 0) {
            recommendations.add("Placement: Place heavy cast iron over trailer axles; keep high-value copper bins secured in truck bed.")
            recommendations.add("Placement: Ensure 60% of trailer weight is in front of the axles to prevent sway.")
        }
        if (loadedTrailerWeight > 0.0 && tonguePercentage !in 10.0..15.0) {
            recommendations.add("CRITICAL: Tongue weight must remain between 10% and 15% of loaded trailer weight.")
        }

        return PayloadSafetyResult(
            truckPayloadPercentage = truckPercentage,
            trailerPayloadPercentage = trailerPercentage,
            isTruckOverweight = isTruckOverweight,
            isTrailerOverweight = isTrailerOverweight,
            loadedTrailerWeightLbs = loadedTrailerWeight,
            tongueWeightPercentage = tonguePercentage,
            minimumSafeTongueWeightLbs = minimumTongue,
            maximumSafeTongueWeightLbs = maximumTongue,
            recommendations = recommendations
        )
    }
}
