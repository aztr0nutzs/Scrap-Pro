package com.example.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.example.data.domain.ScrapYardLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.math.*

class LocationRepository(
    private val fusedLocationClient: FusedLocationProviderClient
) {
    
    // Seed Dataset
    private val seedYards = listOf(
        ScrapYardLocation(
            id = "yard_1",
            name = "Apex Metals Recycling",
            address = "123 Industrial Blvd, Cityville",
            latitude = 40.7128,
            longitude = -74.0060,
            phoneNumber = "555-0101",
            website = "www.apexmetals.com",
            googleRating = 4.5f,
            distanceMiles = 0.0,
            openingHours = DayOfWeek.values().associateWith { Pair(LocalTime.of(8, 0), LocalTime.of(16, 30)) },
            acceptsFerrous = true,
            acceptsNonFerrous = true,
            acceptsVehicles = false,
            acceptsEWaste = true,
            hasTruckScale = true,
            containerRentalAvailable = true,
            payoutType = "Cash & Check",
            requiredID = "State ID",
            safetyGearRequired = listOf("Hard Hat", "High-Vis Vest"),
            prohibitedItems = listOf("Sealed Tanks", "Stolen Material")
        ),
        ScrapYardLocation(
            id = "yard_2",
            name = "Steel City Scrap",
            address = "456 River Road, Townsburg",
            latitude = 40.7300,
            longitude = -74.0150,
            phoneNumber = "555-0202",
            website = "www.steelcityscrap.com",
            googleRating = 4.2f,
            distanceMiles = 0.0,
            openingHours = DayOfWeek.values().associateWith { Pair(LocalTime.of(7, 30), LocalTime.of(17, 0)) },
            acceptsFerrous = true,
            acceptsNonFerrous = true,
            acceptsVehicles = true,
            acceptsEWaste = false,
            hasTruckScale = true,
            containerRentalAvailable = false,
            payoutType = "Check",
            requiredID = "Driver's License",
            safetyGearRequired = listOf("Steel-Toe Boots"),
            prohibitedItems = listOf("Capacitors", "Freon containing appliances without certificate")
        ),
        ScrapYardLocation(
            id = "yard_3",
            name = "Quick Cash Non-Ferrous",
            address = "789 Main St, Villageton",
            latitude = 40.7050,
            longitude = -73.9950,
            phoneNumber = "555-0303",
            website = "www.quickcashscrap.com",
            googleRating = 4.8f,
            distanceMiles = 0.0,
            openingHours = DayOfWeek.values().associateWith { Pair(LocalTime.of(9, 0), LocalTime.of(15, 0)) },
            acceptsFerrous = false,
            acceptsNonFerrous = true,
            acceptsVehicles = false,
            acceptsEWaste = false,
            hasTruckScale = false,
            containerRentalAvailable = false,
            payoutType = "Cash",
            requiredID = "State ID",
            safetyGearRequired = listOf(),
            prohibitedItems = listOf("Ferrous materials", "Hazardous waste")
        )
    )

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getNearbyYards(currentLat: Double, currentLng: Double): List<ScrapYardLocation> {
        // Fallback to a default location (e.g., NYC) if parameters are 0.0
        val baseLat = if (currentLat == 0.0) 40.7128 else currentLat
        val baseLng = if (currentLng == 0.0) -74.0060 else currentLng
        
        return seedYards.map { yard ->
            val distance = calculateDistanceMiles(baseLat, baseLng, yard.latitude, yard.longitude)
            yard.copy(distanceMiles = distance)
        }.sortedBy { it.distanceMiles }
    }

    private fun calculateDistanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 3958.8 // Radius of Earth in miles
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
