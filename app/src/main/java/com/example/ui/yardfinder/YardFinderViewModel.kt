package com.example.ui.yardfinder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.domain.ScrapYardLocation
import com.example.data.domain.YardFilters
import com.example.data.domain.YardPrice
import com.example.data.domain.distanceMiles
import com.example.data.local.ScrapProDatabase
import com.example.data.repository.LocationRepository
import com.example.data.repository.LocationResult
import com.example.data.repository.YardRepository
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

sealed interface LocationUiState {
    data object NotRequested : LocationUiState
    data object RequestPermission : LocationUiState
    data object Loading : LocationUiState
    data class Active(val approximate: Boolean) : LocationUiState
    data object Denied : LocationUiState
    data object PermanentlyDenied : LocationUiState
    data object ServicesDisabled : LocationUiState
    data object Unavailable : LocationUiState
}

data class YardFinderUiState(
    val yards: List<ScrapYardLocation> = emptyList(),
    val visibleYards: List<ScrapYardLocation> = emptyList(),
    val prices: Map<Long, List<YardPrice>> = emptyMap(),
    val search: String = "",
    val filters: YardFilters = YardFilters(),
    val location: LocationUiState = LocationUiState.NotRequested,
    val selectedYard: ScrapYardLocation? = null,
    val message: String? = null
)

class YardFinderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = YardRepository(ScrapProDatabase.getDatabase(application).yardDao())
    private val locationRepository = LocationRepository(LocationServices.getFusedLocationProviderClient(application))
    private val controls = MutableStateFlow(Controls())

    val uiState: StateFlow<YardFinderUiState> = combine(
        repository.observeYards(), repository.observePrices(), controls
    ) { yards, prices, control ->
        val located = yards.map { yard ->
            control.coordinates?.let { (lat, lng) ->
                yard.copy(distanceMiles = distanceMiles(lat, lng, yard.latitude, yard.longitude))
            } ?: yard
        }
        val visible = located.filter { it.matches(control.search, control.filters, LocalDateTime.now()) }
            .sortedWith(compareByDescending<ScrapYardLocation> { it.favorite }.thenBy { it.distanceMiles ?: Double.MAX_VALUE }.thenBy { it.name })
        YardFinderUiState(located, visible, prices, control.search, control.filters, control.location, control.selected, control.message)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), YardFinderUiState())

    fun updateSearch(value: String) = update { copy(search = value) }
    fun updateFilters(value: YardFilters) = update { copy(filters = value) }
    fun requestLocationPermission() = update { copy(location = LocationUiState.RequestPermission, message = null) }

    fun useLocation(hasCoarse: Boolean, hasFine: Boolean, servicesEnabled: Boolean, permanentlyDenied: Boolean) {
        if (!hasCoarse) {
            update { copy(location = if (permanentlyDenied) LocationUiState.PermanentlyDenied else LocationUiState.Denied) }
            return
        }
        update { copy(location = LocationUiState.Loading, message = null) }
        viewModelScope.launch {
            when (val result = locationRepository.currentLocation(true, hasFine, servicesEnabled)) {
                is LocationResult.Available -> update { copy(coordinates = result.latitude to result.longitude, location = LocationUiState.Active(!result.precise)) }
                LocationResult.ServicesDisabled -> update { copy(location = LocationUiState.ServicesDisabled) }
                LocationResult.Unavailable -> update { copy(location = LocationUiState.Unavailable) }
                LocationResult.PermissionDenied -> update { copy(location = LocationUiState.Denied) }
            }
        }
    }

    fun select(yard: ScrapYardLocation?) = update { copy(selected = yard) }
    fun saveYard(yard: ScrapYardLocation) = launch("Yard saved") { repository.saveYard(yard) }
    fun deleteYard(yard: ScrapYardLocation) = launch("Yard deleted") { repository.deleteYard(yard) }
    fun toggleFavorite(yard: ScrapYardLocation) = launch(null) { repository.toggleFavorite(yard) }
    fun importStarterDataset() = launch("Bundled starter templates imported; edit them before use") { repository.importStarterDataset() }
    fun savePrice(price: YardPrice) = launch("Price saved as user-reported data") { repository.savePrice(price) }
    fun deletePrice(price: YardPrice) = launch("Price deleted") { repository.deletePrice(price) }
    fun showMessage(message: String) = update { copy(message = message) }
    fun clearMessage() = update { copy(message = null) }

    private fun launch(message: String?, block: suspend () -> Unit) = viewModelScope.launch {
        runCatching { block() }.fold(
            onSuccess = { update { copy(message = message) } },
            onFailure = { error -> update { copy(message = error.message ?: "Operation failed") } }
        )
    }
    private fun update(block: Controls.() -> Controls) { controls.value = controls.value.block() }

    private data class Controls(
        val search: String = "",
        val filters: YardFilters = YardFilters(),
        val coordinates: Pair<Double, Double>? = null,
        val location: LocationUiState = LocationUiState.NotRequested,
        val selected: ScrapYardLocation? = null,
        val message: String? = null
    )
}
