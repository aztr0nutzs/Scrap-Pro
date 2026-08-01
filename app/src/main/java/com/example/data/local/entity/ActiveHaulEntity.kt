package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_haul")
data class ActiveHaulEntity(
    @PrimaryKey val id: Long = SINGLETON_ID,
    val fuelCost: Double,
    val otherExpenses: Double,
    val laborHours: Double,
    val hourlyLaborRate: Double,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        const val SINGLETON_ID = 1L
    }
}
