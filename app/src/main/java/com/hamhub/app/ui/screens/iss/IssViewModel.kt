package com.hamhub.app.ui.screens.iss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.api.IssApi
import com.hamhub.app.data.remote.dto.N2yoPass
import com.hamhub.app.data.repository.SettingsRepository
import com.hamhub.app.domain.util.GridSquareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val gridSquare: String = "",
    val satName: String = "ISS (ZARYA)",
    val lastUpdate: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val apiKeyConfigured: Boolean = false,
    val passes: List<N2yoPass> = emptyList()
)

@HiltViewModel
class IssViewModel @Inject constructor(
    private val issApi: IssApi,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    // Default observer location (center of USA - can be overridden)
    private var observerLat = 39.8283
    private var observerLng = -98.5795

    // Track the polling job to prevent duplicates
    private var trackingJob: Job? = null

    init {
        checkApiKeyAndStart()
    }

    private fun checkApiKeyAndStart() {
        viewModelScope.launch {
            val apiKey = settingsRepository.getN2yoApiKey()
            if (apiKey.isNullOrBlank()) {
                _uiState.value = IssUiState(
                    isLoading = false,
                    apiKeyConfigured = false,
                    error = "N2YO API key required"
                )
            } else {
                _uiState.value = _uiState.value.copy(apiKeyConfigured = true)
                startTracking()
            }
        }
    }

    private fun startTracking() {
        // Cancel any existing tracking job to prevent duplicates
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            while (isActive) {
                fetchPosition()
                delay(10000) // Update every 10 seconds (N2YO has rate limits)
            }
        }
    }

    private suspend fun fetchPosition() {
        val apiKey = settingsRepository.getN2yoApiKey()
        if (apiKey.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                apiKeyConfigured = false,
                error = "N2YO API key required"
            )
            return
        }

        try {
            val response = issApi.getPositions(
                id = IssApi.ISS_NORAD_ID,
                observerLat = observerLat,
                observerLng = observerLng,
                apiKey = apiKey
            )

            val position = response.positions.firstOrNull()
            if (position != null) {
                val grid = GridSquareUtils.latLngToGrid(position.satlatitude, position.satlongitude)

                _uiState.value = _uiState.value.copy(
                    latitude = position.satlatitude,
                    longitude = position.satlongitude,
                    altitude = position.sataltitude,
                    gridSquare = grid,
                    satName = response.info.satname.ifBlank { "ISS (ZARYA)" },
                    lastUpdate = position.timestamp,
                    isLoading = false,
                    apiKeyConfigured = true,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to fetch ISS position"
            )
        }
    }

    fun setObserverLocation(lat: Double, lng: Double) {
        observerLat = lat
        observerLng = lng
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fetchPosition()
        }
    }

    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsRepository.setN2yoApiKey(apiKey)
            _uiState.value = _uiState.value.copy(
                apiKeyConfigured = true,
                isLoading = true
            )
            fetchPosition()
            startTracking()
        }
    }

    fun fetchPasses() {
        viewModelScope.launch {
            val apiKey = settingsRepository.getN2yoApiKey()
            if (apiKey.isNullOrBlank()) return@launch

            try {
                val response = issApi.getRadioPasses(
                    id = IssApi.ISS_NORAD_ID,
                    observerLat = observerLat,
                    observerLng = observerLng,
                    apiKey = apiKey
                )
                _uiState.value = _uiState.value.copy(passes = response.passes)
            } catch (e: Exception) {
                // Silently fail - passes are optional
            }
        }
    }
}
