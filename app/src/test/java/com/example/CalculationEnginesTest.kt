package com.example

import com.example.domain.engine.PayloadSafetyEngine
import com.example.domain.engine.ProfitCalculationEngine
import com.example.domain.engine.WireStrippingRoiEngine
import com.example.domain.model.CargoLoad
import com.example.domain.model.LoadItem
import com.example.domain.model.MetalGrade
import com.example.domain.model.ProcessingExpense
import com.example.domain.model.ScrapItem
import com.example.domain.model.VehicleRating
import com.example.domain.model.WireCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculationEnginesTest {
    @Test
    fun emptyAndZeroInputsRemainFinite() {
        val profit = ProfitCalculationEngine().calculateProfit(LoadItem(emptyList(), ProcessingExpense()))
        val wire = WireStrippingRoiEngine().calculateRoi(WireCategory.ROMEX, 0.0, 0.0, 0.0, 0.0)
        val payload = PayloadSafetyEngine().evaluatePayloadSafety(
            VehicleRating(0.0, 0.0, 0.0),
            CargoLoad(0.0, 0.0, 0.0)
        )
        assertEquals(0.0, profit.netProfit, 0.0)
        assertEquals(0.0, wire.effectiveHourlyWage, 0.0)
        assertEquals(0.0, payload.truckPayloadPercentage, 0.0)
        assertEquals(0.0, payload.trailerPayloadPercentage, 0.0)
        assertEquals(0.0, payload.tongueWeightPercentage, 0.0)
    }

    @Test
    fun dirtyYieldAndExpensesProduceExpectedNet() {
        val result = ProfitCalculationEngine().calculateProfit(
            LoadItem(
                items = listOf(ScrapItem(MetalGrade.BARE_BRIGHT_COPPER, 100.0, 4.0, false, 50.0)),
                expense = ProcessingExpense(fuelCost = 10.0)
            )
        )
        assertEquals(140.0, result.grossPayout, 0.001)
        assertEquals(130.0, result.netProfit, 0.001)
        assertEquals(260.0, result.valueLossWarning, 0.001)
    }

    @Test
    fun tongueWeightSafeZoneIsCalculatedFromLoadedTrailer() {
        val result = PayloadSafetyEngine().evaluatePayloadSafety(
            VehicleRating(1500.0, 7000.0, 2000.0),
            CargoLoad(1000.0, 3000.0, 600.0)
        )
        assertEquals(12.0, result.tongueWeightPercentage, 0.001)
        assertEquals(500.0, result.minimumSafeTongueWeightLbs, 0.001)
        assertEquals(750.0, result.maximumSafeTongueWeightLbs, 0.001)
        assertTrue(result.recommendations.none { it.contains("Tongue weight must") })
    }
}
