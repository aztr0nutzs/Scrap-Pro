package com.example.data.repository

import com.example.data.local.dao.TripLogDao
import com.example.data.local.entity.TripLogEntity
import kotlinx.coroutines.flow.Flow

class TripLogRepository(private val tripLogDao: TripLogDao) {
    
    fun getAllTripLogsSortedByDate(): Flow<List<TripLogEntity>> = tripLogDao.getAllTripLogsSortedByDate()
    
    fun getAllTripLogsSortedByProfit(): Flow<List<TripLogEntity>> = tripLogDao.getAllTripLogsSortedByProfit()
    
    suspend fun insertTripLog(tripLog: TripLogEntity) {
        tripLogDao.insertTripLog(tripLog)
    }
    
    suspend fun updateTripLog(tripLog: TripLogEntity) {
        tripLogDao.updateTripLog(tripLog)
    }
    
    suspend fun deleteTripLog(tripLog: TripLogEntity) {
        tripLogDao.deleteTripLog(tripLog)
    }
}
