package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.MetalGrade

@Entity(
    tableName = "active_haul_items",
    foreignKeys = [
        ForeignKey(
            entity = ActiveHaulEntity::class,
            parentColumns = ["id"],
            childColumns = ["haulId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("haulId")]
)
data class ActiveHaulItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val haulId: Long = ActiveHaulEntity.SINGLETON_ID,
    val position: Int,
    val metalGrade: MetalGrade,
    val weightLbs: Double,
    val pricePerLb: Double,
    val isCleaned: Boolean,
    val recoverableYieldPercent: Double
)
