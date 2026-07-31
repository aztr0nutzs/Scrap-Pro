package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScrapProDatabase
import com.example.data.repository.HomeRepository
import com.example.data.repository.RoomHomeRepository
import com.example.domain.model.ActiveHaul
import com.example.domain.model.RecentHaulActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Empty(val recentActivity: List<RecentHaulActivity>) : HomeUiState
    data class Populated(
        val activeHaul: ActiveHaul,
        val recentActivity: List<RecentHaulActivity>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeViewModel private constructor(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application,
        RoomHomeRepository(ScrapProDatabase.getDatabase(application).homeDao())
    )

    private val operationError = MutableStateFlow<String?>(null)
    private val retryGeneration = MutableStateFlow(0)

    val uiState: StateFlow<HomeUiState> = retryGeneration
        .flatMapLatest {
            combine(
                repository.observeActiveHaul(),
                repository.observeRecentActivity(),
                operationError
            ) { activeHaul, recentActivity, error ->
                when {
                    error != null -> HomeUiState.Error(error)
                    activeHaul == null -> HomeUiState.Empty(recentActivity)
                    else -> HomeUiState.Populated(activeHaul, recentActivity)
                }
            }
                .catch { emit(HomeUiState.Error(it.message ?: "Unable to load dashboard data")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    fun startHaul() = execute("Unable to start a haul") { repository.startHaul() }

    fun completeHaul() = execute("Unable to complete the haul") {
        repository.completeActiveHaul()
    }

    fun clearHaul() = execute("Unable to clear the haul") { repository.clearActiveHaul() }

    fun retry() {
        operationError.value = null
        retryGeneration.value += 1
    }

    private fun execute(message: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            operationError.value = null
            runCatching { action() }
                .onFailure { operationError.value = it.message ?: message }
        }
    }
}
