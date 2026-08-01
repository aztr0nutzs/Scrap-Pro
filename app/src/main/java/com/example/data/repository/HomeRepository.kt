package com.example.data.repository

import com.example.domain.model.ActiveHaul
import com.example.domain.model.ProcessingExpense
import com.example.domain.model.RecentHaulActivity
import com.example.domain.model.ScrapItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeActiveHaul(): Flow<ActiveHaul?>
    fun observeRecentActivity(limit: Int = 10): Flow<List<RecentHaulActivity>>
    suspend fun startHaul()
    suspend fun saveActiveHaul(items: List<ScrapItem>, expense: ProcessingExpense)
    suspend fun completeActiveHaul()
    suspend fun clearActiveHaul()
}
