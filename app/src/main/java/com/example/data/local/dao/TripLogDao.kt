package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.TripLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripLog(tripLog: TripLogEntity): Long

    @Update
    suspend fun updateTripLog(tripLog: TripLogEntity)

    @Delete
    suspend fun deleteTripLog(tripLog: TripLogEntity)

    @Query("SELECT * FROM trip_logs ORDER BY date DESC")
    fun getAllTripLogsSortedByDate(): Flow<List<TripLogEntity>>

    @Query("SELECT * FROM trip_logs ORDER BY netProfit DESC")
    fun getAllTripLogsSortedByProfit(): Flow<List<TripLogEntity>>
}
