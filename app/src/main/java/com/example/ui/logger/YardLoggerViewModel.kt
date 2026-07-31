package com.example.ui.logger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScrapProDatabase
import com.example.data.local.entity.TripLogEntity
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity
import com.example.data.repository.TripLogRepository
import com.example.data.repository.YardRepository
import com.example.ui.core.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class YardLoggerUiState(
    val yards: List<YardEntity> = emptyList(),
    val tripLogs: List<TripLogEntity> = emptyList(),
    val currentPrices: List<YardPriceEntity> = emptyList()
)

class YardLoggerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ScrapProDatabase.getDatabase(application)
    private val yardRepo = YardRepository(db.yardDao())
    private val tripLogRepo = TripLogRepository(db.tripLogDao())

    private val _uiState = MutableStateFlow<UiState<YardLoggerUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<YardLoggerUiState>> = _uiState.asStateFlow()
    
    private var currentState = YardLoggerUiState()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            yardRepo.getAllYards()
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Error loading yards") }
                .collect { yards ->
                    currentState = currentState.copy(yards = yards)
                    _uiState.value = UiState.Success(currentState)
                }
        }
        
        viewModelScope.launch {
            tripLogRepo.getAllTripLogsSortedByDate()
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Error loading trips") }
                .collect { logs ->
                    currentState = currentState.copy(tripLogs = logs)
                    _uiState.value = UiState.Success(currentState)
                }
        }
    }

    fun fetchPricesForMetal(metalGrade: com.example.domain.model.MetalGrade) {
        viewModelScope.launch {
            yardRepo.getPricesForMetal(metalGrade)
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Error loading prices") }
                .collect { prices ->
                    currentState = currentState.copy(currentPrices = prices)
                    _uiState.value = UiState.Success(currentState)
                }
        }
    }

    fun logTrip(yardId: Long?, gross: Double, expenses: Double, net: Double, weight: Double, notes: String) {
        viewModelScope.launch {
            try {
                val log = TripLogEntity(
                    date = System.currentTimeMillis(),
                    yardId = yardId,
                    totalGrossPayout = gross,
                    totalExpenses = expenses,
                    netProfit = net,
                    totalWeightLbs = weight,
                    receiptUri = null,
                    notes = notes
                )
                tripLogRepo.insertTripLog(log)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to save trip: ${e.message}")
            }
        }
    }
}
