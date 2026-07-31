package com.example.data.repository

import com.example.data.local.dao.YardDao
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity
import com.example.domain.model.MetalGrade
import kotlinx.coroutines.flow.Flow

class YardRepository(private val yardDao: YardDao) {
    
    fun getAllYards(): Flow<List<YardEntity>> = yardDao.getAllYards()
    
    fun getYardPrices(yardId: Long): Flow<List<YardPriceEntity>> = yardDao.getYardPrices(yardId)
    
    fun getPricesForMetal(metalGrade: MetalGrade): Flow<List<YardPriceEntity>> = yardDao.getPricesForMetal(metalGrade)
    
    suspend fun insertYard(yard: YardEntity) {
        yardDao.insertYard(yard)
    }
    
    suspend fun updateYard(yard: YardEntity) {
        yardDao.updateYard(yard)
    }
    
    suspend fun deleteYard(yard: YardEntity) {
        yardDao.deleteYard(yard)
    }
    
    suspend fun insertYardPrice(price: YardPriceEntity) {
        yardDao.insertYardPrice(price)
    }
    
    suspend fun updateYardPrice(price: YardPriceEntity) {
        yardDao.updateYardPrice(price)
    }
}
