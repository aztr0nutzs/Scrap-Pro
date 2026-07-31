package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity
import com.example.domain.model.MetalGrade
import kotlinx.coroutines.flow.Flow

@Dao
interface YardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYard(yard: YardEntity): Long

    @Update
    suspend fun updateYard(yard: YardEntity)

    @Delete
    suspend fun deleteYard(yard: YardEntity)

    @Query("SELECT * FROM yards ORDER BY yardName ASC")
    fun getAllYards(): Flow<List<YardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYardPrice(price: YardPriceEntity): Long

    @Update
    suspend fun updateYardPrice(price: YardPriceEntity)

    @Query("SELECT * FROM yard_prices WHERE yardId = :yardId ORDER BY metalGrade ASC")
    fun getYardPrices(yardId: Long): Flow<List<YardPriceEntity>>

    @Query("SELECT * FROM yard_prices WHERE metalGrade = :metalGrade ORDER BY pricePerLb DESC")
    fun getPricesForMetal(metalGrade: MetalGrade): Flow<List<YardPriceEntity>>
}
