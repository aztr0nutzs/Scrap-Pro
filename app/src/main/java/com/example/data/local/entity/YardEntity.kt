package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yards")
data class YardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yardName: String,
    val address: String,
    val distanceMiles: Double,
    val phone: String,
    val notes: String
)
