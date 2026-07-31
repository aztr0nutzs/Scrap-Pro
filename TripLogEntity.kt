package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trip_logs",
    foreignKeys = [
        ForeignKey(
            entity = YardEntity::class,
            parentColumns = ["id"],
            childColumns = ["yardId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("yardId")]
)
data class TripLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val yardId: Long?,
    val totalGrossPayout: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val totalWeightLbs: Double,
    val receiptUri: String?,
    val notes: String
)
