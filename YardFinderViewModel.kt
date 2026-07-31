package com.example.ui.yardfinder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.domain.ScrapYardLocation
import com.example.data.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class YardFinderUiState(
    val yards: List<ScrapYardLocation> = emptyList(),
    val filteredYards: List<ScrapYardLocation> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val searchRadius: Int = 25, // miles
    val filterOpenNow: Boolean = false,
    val filterTruckScale: Boolean = false,
    val filterCashPayout: Boolean = false,
    val filterNonFerrous: Boolean = false,
    val isMapView: Boolean = false,
    val selectedYard: ScrapYardLocation? = null,
    val currentLocation: Pair<Double, Double> = Pair(0.0, 0.0)
)

class YardFinderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val repository = LocationRepository(fusedLocationClient)
    
    private val _uiState = MutableStateFlow(YardFinderUiState())
    val uiState: StateFlow<YardFinderUiState> = _uiState.asStateFlow()

    init {
        fetchLocationsWithPermission()
    }

    fun fetchLocationsWithPermission() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val location = repository.getCurrentLocation()
            
            val lat = location?.latitude ?: 40.7128 // Default to NYC for simulation
            val lng = location?.longitude ?: -74.0060
            
            _uiState.value = _uiState.value.copy(currentLocation = Pair(lat, lng))
            
            val yards = repository.getNearbyYards(lat, lng)
            
            _uiState.value = _uiState.value.copy(
                yards = yards,
                isLoading = false
            )
            applyFilters()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun updateRadius(radius: Int) {
        _uiState.value = _uiState.value.copy(searchRadius = radius)
        applyFilters()
    }

    fun toggleFilterOpenNow() {
        _uiState.value = _uiState.value.copy(filterOpenNow = !_uiState.value.filterOpenNow)
        applyFilters()
    }

    fun toggleFilterTruckScale() {
        _uiState.value = _uiState.value.copy(filterTruckScale = !_uiState.value.filterTruckScale)
        applyFilters()
    }

    fun toggleFilterCashPayout() {
        _uiState.value = _uiState.value.copy(filterCashPayout = !_uiState.value.filterCashPayout)
        applyFilters()
    }

    fun toggleFilterNonFerrous() {
        _uiState.value = _uiState.value.copy(filterNonFerrous = !_uiState.value.filterNonFerrous)
        applyFilters()
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }
    
    fun selectYard(yard: ScrapYardLocation?) {
        _uiState.value = _uiState.value.copy(selectedYard = yard)
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val filtered = currentState.yards.filter { yard ->
            val matchesQuery = currentState.searchQuery.isEmpty() || 
                    yard.name.contains(currentState.searchQuery, ignoreCase = true)
            val matchesRadius = yard.distanceMiles <= currentState.searchRadius
            val matchesOpenNow = !currentState.filterOpenNow || yard.isOpenNow()
            val matchesTruckScale = !currentState.filterTruckScale || yard.hasTruckScale
            val matchesCash = !currentState.filterCashPayout || yard.payoutType.contains("Cash", ignoreCase = true)
            val matchesNonFerrous = !currentState.filterNonFerrous || yard.acceptsNonFerrous

            matchesQuery && matchesRadius && matchesOpenNow && matchesTruckScale && matchesCash && matchesNonFerrous
        }
        _uiState.value = currentState.copy(filteredYards = filtered)
    }
}
