package com.hamhub.app.ui.screens.iss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.api.IssApi
import com.hamhub.app.domain.util.GridSquareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val gridSquare: String = "",
    val lastUpdate: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class IssViewModel @Inject constructor(
    private val issApi: IssApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    init {
        startTracking()
    }

    private fun startTracking() {
        viewModelScope.launch {
            while (isActive) {
                fetchPosition()
                delay(5000) // Update every 5 seconds
            }
        }
    }

    private suspend fun fetchPosition() {
        try {
            val response = issApi.getCurrentPosition()

            if (response.message == "success") {
                val lat = response.issPosition.lat
                val lon = response.issPosition.lon
                val grid = GridSquareUtils.latLngToGrid(lat, lon)

                _uiState.value = IssUiState(
                    latitude = lat,
                    longitude = lon,
                    gridSquare = grid,
                    lastUpdate = response.timestamp,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fetchPosition()
        }
    }
}
