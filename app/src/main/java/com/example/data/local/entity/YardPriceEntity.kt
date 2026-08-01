package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.MetalGrade

@Entity(
    tableName = "yard_prices",
    foreignKeys = [ForeignKey(entity = YardEntity::class, parentColumns = ["id"], childColumns = ["yardId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("yardId")]
)
data class YardPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yardId: Long,
    val metalGrade: MetalGrade,
    val pricePerLb: Double = 0.0,
    val pricePerTon: Double = 0.0,
    val lastUpdatedTimestamp: Long,
    val price: Double = if (pricePerLb > 0) pricePerLb else pricePerTon,
    val unit: String = if (pricePerLb > 0) "lb" else "ton"
)
