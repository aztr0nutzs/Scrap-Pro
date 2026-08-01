package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity
import com.example.domain.model.MetalGrade
import kotlinx.coroutines.flow.Flow

@Dao
interface YardDao {
    @Query("SELECT * FROM yards ORDER BY favorite DESC, yardName ASC")
    fun observeYards(): Flow<List<YardEntity>>

    @Query("SELECT * FROM yard_prices WHERE metalGrade = :metalGrade ORDER BY lastUpdatedTimestamp DESC")
    fun observePricesForMetal(metalGrade: MetalGrade): Flow<List<YardPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertYard(yard: YardEntity): Long

    @Delete
    suspend fun deleteYard(yard: YardEntity)

    @Query("SELECT COUNT(*) FROM yards WHERE bundledStarter = 1")
    suspend fun starterCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrices(prices: List<YardPriceEntity>)

    @Delete
    suspend fun deletePrice(price: YardPriceEntity)

    @Query("SELECT * FROM yard_prices ORDER BY lastUpdatedTimestamp DESC, id DESC")
    fun observePrices(): Flow<List<YardPriceEntity>>
}
