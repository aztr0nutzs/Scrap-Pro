package com.example.domain.model

/** Positions are normalized: -1.0 is forward of the axle group, 0.0 is centered, +1.0 is aft. */
data class TowSafetyInput(
    val vehicleGvwr: Double = 0.0,
    val vehicleCurbWeight: Double = 0.0,
    val ratedPayload: Double = 0.0,
    val frontGawr: Double = 0.0,
    val rearGawr: Double = 0.0,
    val measuredFrontAxle: Double = 0.0,
    val measuredRearAxle: Double = 0.0,
    val receiverRating: Double = 0.0,
    val maxTongueWeight: Double = 0.0,
    val trailerGvwr: Double = 0.0,
    val trailerEmptyWeight: Double = 0.0,
    val trailerAxleRating: Double = 0.0,
    val measuredTrailerAxle: Double = 0.0,
    val trailerCargoWeight: Double = 0.0,
    val measuredTongueWeight: Double = 0.0,
    val passengerCabCargo: Double = 0.0,
    val truckBedCargo: Double = 0.0,
    val truckCargoPosition: Double = 0.5,
    val trailerCargoPosition: Double = -0.2,
    val safetyMarginPercent: Double = 0.0
)
