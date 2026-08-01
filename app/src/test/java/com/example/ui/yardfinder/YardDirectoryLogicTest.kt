package com.example.ui.yardfinder

import com.example.data.domain.*
import com.example.data.repository.LocationResult
import com.example.data.repository.locationPrecondition
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class YardDirectoryLogicTest {
    private val yard = ScrapYardLocation(name="Local", address="1 Main", latitude=40.0, longitude=-74.0, phoneNumber="2125551212", operatingHours="MON=08:00-17:00", temporarilyClosed=false, acceptsFerrous=true, acceptsNonFerrous=false, acceptsVehicles=false, acceptsEWaste=true, cashPayout=true, checkPayout=false, digitalPayout=false, requiredId="ID", hasTruckScale=true, notes="", favorite=true, bundledStarter=false, distanceMiles=5.0)

    @Test fun distanceCalculationIsAccurate() { assertEquals(69.1, distanceMiles(40.0, -74.0, 41.0, -74.0), 0.5) }
    @Test fun hoursHonorDayAndTemporaryClosure() {
        assertTrue(yard.isOpenAt(LocalDateTime.of(2026, 8, 3, 9, 0)))
        assertFalse(yard.copy(temporarilyClosed=true).isOpenAt(LocalDateTime.of(2026, 8, 3, 9, 0)))
    }
    @Test fun filtersAndRadiusAreApplied() {
        assertTrue(yard.matches("main", YardFilters(ferrous=true, eWaste=true, favorites=true, radiusMiles=10.0), LocalDateTime.of(2026,8,3,9,0)))
        assertFalse(yard.matches("", YardFilters(nonFerrous=true), LocalDateTime.of(2026,8,3,9,0)))
    }
    @Test fun stalePriceUsesThirtyDayBoundary() {
        val now=4_000_000_000L; val price=YardPrice(yardId=1, metalGrade="BRASS", price=2.0, unit="lb", observedAt=now-PRICE_STALE_AFTER_MILLIS-1)
        assertTrue(price.isStale(now)); assertFalse(price.copy(observedAt=now-1000).isStale(now))
    }
    @Test fun permissionAndServicesAreClassifiedBeforeLocationAccess() {
        assertEquals(LocationResult.PermissionDenied, locationPrecondition(false, true))
        assertEquals(LocationResult.ServicesDisabled, locationPrecondition(true, false))
        assertNull(locationPrecondition(true, true))
    }
}
