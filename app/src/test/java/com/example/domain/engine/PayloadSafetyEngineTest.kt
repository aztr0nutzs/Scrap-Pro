package com.example.domain.engine

import com.example.domain.model.TowSafetyInput
import org.junit.Assert.*
import org.junit.Test

class PayloadSafetyEngineTest {
 private val engine=PayloadSafetyEngine()
 private fun base(tongue:Double=125.0)=TowSafetyInput(vehicleGvwr=7000.0,vehicleCurbWeight=5000.0,ratedPayload=2000.0,frontGawr=4000.0,rearGawr=4200.0,measuredFrontAxle=2800.0,measuredRearAxle=2200.0,receiverRating=800.0,maxTongueWeight=700.0,trailerGvwr=5000.0,trailerEmptyWeight=500.0,trailerAxleRating=4500.0,trailerCargoWeight=500.0,measuredTongueWeight=tongue)
 @Test fun emptyAndZeroRatingsStayFinite(){ val r=engine.evaluate(TowSafetyInput()); assertTrue(r.allFinite()); assertTrue(r.fieldErrors.isNotEmpty()) }
 @Test fun negativeInputsAreRejectedAndSanitized(){ val r=engine.evaluate(base().copy(truckBedCargo=-10.0)); assertEquals("Cannot be negative",r.fieldErrors["truckBedCargo"]); assertTrue(r.allFinite()) }
 @Test fun emptyTrailerAboveGvwrIsInvalid(){ assertNotNull(engine.evaluate(base().copy(trailerGvwr=400.0,trailerEmptyWeight=500.0)).fieldErrors["trailerEmptyWeight"]) }
 @Test fun exactTongueBoundaries(){ listOf(99.0 to SafetyClass.YELLOW,100.0 to SafetyClass.GREEN,125.0 to SafetyClass.GREEN,150.0 to SafetyClass.GREEN,151.0 to SafetyClass.YELLOW).forEach { (w,c)->val r=engine.evaluate(base(w));assertEquals(w/10.0,r.tongueWeightPercentage,0.001);assertEquals(c,r.tongueStatus)} }
 @Test fun hitchOverloadIsRed(){ assertEquals(SafetyClass.RED,engine.evaluate(base(900.0)).hitchStatus.classification) }
 @Test fun rearAxleCanOverloadWhileGvwrIsUnder(){ val r=engine.evaluate(base().copy(measuredRearAxle=4100.0,truckBedCargo=200.0,truckCargoPosition=1.0));assertEquals(SafetyClass.RED,r.rearAxleStatus.classification);assertNotEquals(SafetyClass.RED,r.vehicleStatus.classification) }
 @Test fun trailerAxleOverloadIsRed(){ assertEquals(SafetyClass.RED,engine.evaluate(base().copy(measuredTrailerAxle=4600.0)).trailerAxleStatus.classification) }
 @Test fun hugeInputsRemainFiniteAndOverloadsAreNotClamped(){ val r=engine.evaluate(base().copy(vehicleGvwr=1e200,vehicleCurbWeight=1e199,ratedPayload=1e200,truckBedCargo=2e200));assertTrue(r.allFinite());assertTrue(r.vehicleStatus.percentage>100.0) }
 private fun PayloadSafetyResult.allFinite()=listOf(availableVehiclePayload,loadedVehicleWeight,estimatedFrontAxleLoad,estimatedRearAxleLoad,trailerPayloadCapacity,loadedTrailerWeight,estimatedTrailerAxleLoad,tongueWeightPercentage,minimumSafeTongueWeight,maximumSafeTongueWeight,hitchStatus.percentage,vehicleStatus.percentage,frontAxleStatus.percentage,rearAxleStatus.percentage,trailerStatus.percentage,trailerAxleStatus.percentage).all(Double::isFinite)
}
