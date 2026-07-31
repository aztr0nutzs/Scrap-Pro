package com.example.data.domain

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class ScrapYardLocation(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val website: String,
    val googleRating: Float,
    val distanceMiles: Double,
    
    val openingHours: Map<DayOfWeek, Pair<LocalTime, LocalTime>>,
    
    // Facility Features
    val acceptsFerrous: Boolean,
    val acceptsNonFerrous: Boolean,
    val acceptsVehicles: Boolean,
    val acceptsEWaste: Boolean,
    val hasTruckScale: Boolean,
    val containerRentalAvailable: Boolean,
    val payoutType: String, // "Cash", "Check", "Digital"
    
    // Requirements & Rules
    val requiredID: String,
    val safetyGearRequired: List<String>,
    val prohibitedItems: List<String>
) {
    fun isOpenNow(): Boolean {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val dayOfWeek = now.dayOfWeek
        val currentTime = now.toLocalTime()
        
        val hoursToday = openingHours[dayOfWeek] ?: return false
        val openTime = hoursToday.first
        val closeTime = hoursToday.second
        
        return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
    }
}
