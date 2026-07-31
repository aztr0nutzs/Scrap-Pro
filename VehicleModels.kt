package com.example.domain.model

data class VehicleRating(
    val truckMaxPayloadLbs: Double,
    val trailerGvwrLbs: Double,
    val trailerEmptyWeightLbs: Double
) {
    val trailerMaxPayloadLbs: Double get() = (trailerGvwrLbs - trailerEmptyWeightLbs).coerceAtLeast(0.0)
}

data class CargoLoad(
    val truckBedCargoWeightLbs: Double,
    val trailerCargoWeightLbs: Double,
    val tongueWeightLbs: Double = 0.0
)
