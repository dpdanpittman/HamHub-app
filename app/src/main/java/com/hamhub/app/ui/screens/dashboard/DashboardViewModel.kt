package com.hamhub.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.repository.DashboardStats
import com.hamhub.app.data.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val stats: DashboardStats = DashboardStats(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            statsRepository.getDashboardStats().collect { stats ->
                _uiState.value = DashboardUiState(
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadStats()
    }
}
