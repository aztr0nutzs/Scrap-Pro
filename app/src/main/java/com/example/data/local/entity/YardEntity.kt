package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yards")
data class YardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yardName: String,
    val address: String,
    val distanceMiles: Double = 0.0,
    val phone: String,
    val notes: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val operatingHours: String = "",
    val temporarilyClosed: Boolean = false,
    val acceptsFerrous: Boolean = false,
    val acceptsNonFerrous: Boolean = false,
    val acceptsVehicles: Boolean = false,
    val acceptsEWaste: Boolean = false,
    val cashPayout: Boolean = false,
    val checkPayout: Boolean = false,
    val digitalPayout: Boolean = false,
    val requiredId: String = "",
    val hasTruckScale: Boolean = false,
    val favorite: Boolean = false,
    val bundledStarter: Boolean = false
)
