package com.example.data.repository

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

sealed interface LocationResult {
    data class Available(val latitude: Double, val longitude: Double, val precise: Boolean) : LocationResult
    data object PermissionDenied : LocationResult
    data object ServicesDisabled : LocationResult
    data object Unavailable : LocationResult
}

fun locationPrecondition(hasPermission: Boolean, servicesEnabled: Boolean): LocationResult? = when {
    !hasPermission -> LocationResult.PermissionDenied
    !servicesEnabled -> LocationResult.ServicesDisabled
    else -> null
}

class LocationRepository(private val client: FusedLocationProviderClient) {
    suspend fun currentLocation(hasPermission: Boolean, precise: Boolean, servicesEnabled: Boolean): LocationResult {
        locationPrecondition(hasPermission, servicesEnabled)?.let { return it }
        val location: Location = runCatching {
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
        }.getOrNull() ?: return LocationResult.Unavailable
        return LocationResult.Available(location.latitude, location.longitude, precise)
    }
}
