package com.example.domain.engine

import com.example.domain.model.TowSafetyInput

enum class SafetyClass { GREEN, YELLOW, RED }

data class LimitStatus(val current: Double, val limit: Double, val percentage: Double, val classification: SafetyClass)

data class PayloadSafetyResult(
    val availableVehiclePayload: Double,
    val loadedVehicleWeight: Double,
    val estimatedFrontAxleLoad: Double,
    val estimatedRearAxleLoad: Double,
    val trailerPayloadCapacity: Double,
    val loadedTrailerWeight: Double,
    val estimatedTrailerAxleLoad: Double,
    val tongueWeightPercentage: Double,
    val minimumSafeTongueWeight: Double,
    val maximumSafeTongueWeight: Double,
    val hitchStatus: LimitStatus,
    val vehicleStatus: LimitStatus,
    val frontAxleStatus: LimitStatus,
    val rearAxleStatus: LimitStatus,
    val trailerStatus: LimitStatus,
    val trailerAxleStatus: LimitStatus,
    val tongueStatus: SafetyClass,
    val fieldErrors: Map<String, String>,
    val recommendations: List<String>
)

class PayloadSafetyEngine {
    fun evaluate(input: TowSafetyInput): PayloadSafetyResult {
        val errors = validate(input)
        fun safe(value: Double) = if (value.isFinite()) value.coerceAtLeast(0.0) else 0.0
        val i = input.copy(
            vehicleGvwr=safe(input.vehicleGvwr), vehicleCurbWeight=safe(input.vehicleCurbWeight), ratedPayload=safe(input.ratedPayload),
            frontGawr=safe(input.frontGawr), rearGawr=safe(input.rearGawr), measuredFrontAxle=safe(input.measuredFrontAxle), measuredRearAxle=safe(input.measuredRearAxle),
            receiverRating=safe(input.receiverRating), maxTongueWeight=safe(input.maxTongueWeight), trailerGvwr=safe(input.trailerGvwr), trailerEmptyWeight=safe(input.trailerEmptyWeight),
            trailerAxleRating=safe(input.trailerAxleRating), measuredTrailerAxle=safe(input.measuredTrailerAxle), trailerCargoWeight=safe(input.trailerCargoWeight),
            measuredTongueWeight=safe(input.measuredTongueWeight), passengerCabCargo=safe(input.passengerCabCargo), truckBedCargo=safe(input.truckBedCargo),
            truckCargoPosition=input.truckCargoPosition.takeIf(Double::isFinite)?.coerceIn(-1.0,1.0) ?: 0.0,
            trailerCargoPosition=input.trailerCargoPosition.takeIf(Double::isFinite)?.coerceIn(-1.0,1.0) ?: 0.0,
            safetyMarginPercent=safe(input.safetyMarginPercent).coerceAtMost(50.0)
        )
        val margin = 1.0 - i.safetyMarginPercent / 100.0
        val payloadByGvwr = (i.vehicleGvwr - i.vehicleCurbWeight).coerceAtLeast(0.0)
        val payloadLimit = listOf(i.ratedPayload, payloadByGvwr).filter { it > 0.0 }.minOrNull() ?: 0.0
        val carriedPayload = i.passengerCabCargo + i.truckBedCargo + i.measuredTongueWeight
        val availablePayload = (payloadLimit * margin - carriedPayload).coerceAtLeast(0.0)
        val loadedVehicle = i.vehicleCurbWeight + carriedPayload

        val rearShare = (i.truckCargoPosition + 1.0) / 2.0
        val frontCargo = i.truckBedCargo * (1.0 - rearShare)
        val rearCargo = i.truckBedCargo * rearShare
        val estimatedFront = (i.measuredFrontAxle + i.passengerCabCargo * 0.55 + frontCargo - i.measuredTongueWeight * 0.20).coerceAtLeast(0.0)
        val estimatedRear = i.measuredRearAxle + i.passengerCabCargo * 0.45 + rearCargo + i.measuredTongueWeight * 1.20

        val trailerCapacity = (i.trailerGvwr - i.trailerEmptyWeight).coerceAtLeast(0.0)
        val loadedTrailer = i.trailerEmptyWeight + i.trailerCargoWeight
        val trailerAxle = if (i.measuredTrailerAxle > 0) i.measuredTrailerAxle else (loadedTrailer - i.measuredTongueWeight).coerceAtLeast(0.0)
        val tonguePercent = ratio(i.measuredTongueWeight, loadedTrailer)
        val minTongue = loadedTrailer * 0.10
        val maxTongue = loadedTrailer * 0.15
        val tongueClass = when {
            loadedTrailer <= 0.0 -> SafetyClass.YELLOW
            tonguePercent in 10.0..15.0 -> SafetyClass.GREEN
            tonguePercent in 8.0..<10.0 || tonguePercent > 15.0 && tonguePercent <= 17.0 -> SafetyClass.YELLOW
            else -> SafetyClass.RED
        }
        val hitchLimit = listOf(i.receiverRating, i.maxTongueWeight).filter { it > 0 }.minOrNull() ?: 0.0
        val statuses = listOf(
            status(i.measuredTongueWeight, hitchLimit * margin), status(loadedVehicle, i.vehicleGvwr * margin),
            status(estimatedFront, i.frontGawr * margin), status(estimatedRear, i.rearGawr * margin),
            status(loadedTrailer, i.trailerGvwr * margin), status(trailerAxle, i.trailerAxleRating * margin)
        )
        val rec = mutableListOf<String>()
        if (statuses[1].classification == SafetyClass.RED) rec += "Remove vehicle cargo until loaded weight is below the margin-adjusted GVWR."
        if (statuses[2].classification == SafetyClass.RED) rec += "Move vehicle cargo rearward or remove weight to reduce the front axle load."
        if (statuses[3].classification == SafetyClass.RED) rec += "Move truck-bed cargo forward, reduce tongue weight safely, or remove cargo to unload the rear axle."
        if (statuses[0].classification == SafetyClass.RED) rec += "Reduce tongue weight or use a receiver rated for the measured load; never exceed either hitch limit."
        if (statuses[4].classification == SafetyClass.RED) rec += "Remove trailer cargo until loaded trailer weight is below its GVWR."
        if (statuses[5].classification == SafetyClass.RED) rec += "Remove trailer cargo or redistribute it without leaving the 10%–15% tongue range."
        if (tonguePercent < 10.0 && loadedTrailer > 0) rec += "Move trailer cargo forward to increase tongue weight to at least ${minTongue.round()} lb."
        if (tonguePercent > 15.0) rec += "Move trailer cargo rearward carefully to reduce tongue weight to at most ${maxTongue.round()} lb."
        if (i.trailerCargoPosition > 0.3) rec += "Cargo is aft of the trailer axles; move heavy items forward to reduce sway risk."
        if (rec.isEmpty()) rec += "Loads are within entered ratings and margin; confirm all values on labels, manuals, hitch documentation, and certified scales."
        return PayloadSafetyResult(availablePayload, loadedVehicle, estimatedFront, estimatedRear, trailerCapacity, loadedTrailer, trailerAxle,
            tonguePercent, minTongue, maxTongue, statuses[0], statuses[1], statuses[2], statuses[3], statuses[4], statuses[5], tongueClass, errors, rec)
    }

    private fun ratio(value: Double, limit: Double): Double = if (limit > 0.0) (value / limit * 100.0).takeIf(Double::isFinite) ?: 0.0 else 0.0
    private fun status(current: Double, limit: Double): LimitStatus {
        val percent = ratio(current, limit)
        val classification = when { limit <= 0.0 -> SafetyClass.YELLOW; percent > 100.0 -> SafetyClass.RED; percent >= 80.0 -> SafetyClass.YELLOW; else -> SafetyClass.GREEN }
        return LimitStatus(current, limit, percent, classification)
    }
    private fun validate(i: TowSafetyInput): Map<String,String> = buildMap {
        val values = mapOf("vehicleGvwr" to i.vehicleGvwr,"vehicleCurbWeight" to i.vehicleCurbWeight,"ratedPayload" to i.ratedPayload,"frontGawr" to i.frontGawr,"rearGawr" to i.rearGawr,"measuredFrontAxle" to i.measuredFrontAxle,"measuredRearAxle" to i.measuredRearAxle,"receiverRating" to i.receiverRating,"maxTongueWeight" to i.maxTongueWeight,"trailerGvwr" to i.trailerGvwr,"trailerEmptyWeight" to i.trailerEmptyWeight,"trailerAxleRating" to i.trailerAxleRating,"measuredTrailerAxle" to i.measuredTrailerAxle,"trailerCargoWeight" to i.trailerCargoWeight,"measuredTongueWeight" to i.measuredTongueWeight,"passengerCabCargo" to i.passengerCabCargo,"truckBedCargo" to i.truckBedCargo,"safetyMarginPercent" to i.safetyMarginPercent)
        values.forEach { (name,value) -> if (!value.isFinite()) put(name,"Enter a finite number") else if (value < 0) put(name,"Cannot be negative") }
        listOf("vehicleGvwr","ratedPayload","frontGawr","rearGawr","receiverRating","maxTongueWeight","trailerGvwr","trailerAxleRating").forEach { if ((values[it] ?: 0.0) <= 0.0) putIfAbsent(it,"Rating must be greater than zero") }
        if (i.trailerEmptyWeight > i.trailerGvwr && i.trailerGvwr >= 0) put("trailerEmptyWeight","Empty weight exceeds trailer GVWR")
        if (i.truckCargoPosition !in -1.0..1.0) put("truckCargoPosition","Position must be between -1 and 1")
        if (i.trailerCargoPosition !in -1.0..1.0) put("trailerCargoPosition","Position must be between -1 and 1")
        if (i.safetyMarginPercent > 50) put("safetyMarginPercent","Safety margin cannot exceed 50%")
    }
    private fun Double.round() = "%.0f".format(this)
}
