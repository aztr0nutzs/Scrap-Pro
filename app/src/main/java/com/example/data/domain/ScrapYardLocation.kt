package com.example.data.domain

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

const val PRICE_STALE_AFTER_MILLIS = 30L * 24 * 60 * 60 * 1000

data class YardFilters(
    val ferrous: Boolean = false,
    val nonFerrous: Boolean = false,
    val vehicles: Boolean = false,
    val eWaste: Boolean = false,
    val openNow: Boolean = false,
    val truckScale: Boolean = false,
    val cash: Boolean = false,
    val check: Boolean = false,
    val digital: Boolean = false,
    val favorites: Boolean = false,
    val radiusMiles: Double? = null
)

data class ScrapYardLocation(
    val id: Long = 0,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val operatingHours: String,
    val temporarilyClosed: Boolean,
    val acceptsFerrous: Boolean,
    val acceptsNonFerrous: Boolean,
    val acceptsVehicles: Boolean,
    val acceptsEWaste: Boolean,
    val cashPayout: Boolean,
    val checkPayout: Boolean,
    val digitalPayout: Boolean,
    val requiredId: String,
    val hasTruckScale: Boolean,
    val notes: String,
    val favorite: Boolean,
    val bundledStarter: Boolean,
    val distanceMiles: Double? = null,
    val distanceIsFallback: Boolean = false
) {
    fun isOpenAt(dateTime: LocalDateTime): Boolean {
        if (temporarilyClosed) return false
        val token = dateTime.dayOfWeek.name.take(3)
        val entry = operatingHours.split(';').firstOrNull { it.startsWith("$token=") } ?: return false
        val range = entry.substringAfter('=').split('-')
        if (range.size != 2) return false
        val open = runCatching { LocalTime.parse(range[0]) }.getOrNull() ?: return false
        val close = runCatching { LocalTime.parse(range[1]) }.getOrNull() ?: return false
        val now = dateTime.toLocalTime()
        return !now.isBefore(open) && now.isBefore(close)
    }

    fun matches(query: String, filters: YardFilters, now: LocalDateTime): Boolean {
        val textMatches = query.isBlank() || name.contains(query, true) || address.contains(query, true)
        val radiusMatches = filters.radiusMiles == null || distanceMiles?.let { it <= filters.radiusMiles } == true
        return textMatches && radiusMatches &&
            (!filters.ferrous || acceptsFerrous) && (!filters.nonFerrous || acceptsNonFerrous) &&
            (!filters.vehicles || acceptsVehicles) && (!filters.eWaste || acceptsEWaste) &&
            (!filters.openNow || isOpenAt(now)) && (!filters.truckScale || hasTruckScale) &&
            (!filters.cash || cashPayout) && (!filters.check || checkPayout) &&
            (!filters.digital || digitalPayout) && (!filters.favorites || favorite)
    }
}

data class YardPrice(
    val id: Long = 0,
    val yardId: Long,
    val metalGrade: String,
    val price: Double,
    val unit: String,
    val observedAt: Long
) {
    fun isStale(now: Long): Boolean = now - observedAt > PRICE_STALE_AFTER_MILLIS
}

fun distanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radius = 3958.8
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) *
        cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    return radius * 2 * atan2(sqrt(a), sqrt(1 - a))
}
