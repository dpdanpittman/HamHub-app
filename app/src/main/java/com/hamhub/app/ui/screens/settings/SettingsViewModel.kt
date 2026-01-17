package com.hamhub.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.repository.SettingsRepository
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val myCallsign: String = "",
    val myGrid: String = "",
    val theme: String = "system",
    val defaultPower: String = "",
    val defaultMode: String = "",
    val defaultBand: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                settingsRepository.getMyCallsignFlow(),
                settingsRepository.getMyGridFlow(),
                settingsRepository.getThemeFlow(),
                settingsRepository.getDefaultPowerFlow(),
                settingsRepository.getDefaultModeFlow(),
                settingsRepository.getDefaultBandFlow()
            ) { values: Array<Any?> ->
                SettingsUiState(
                    myCallsign = (values[0] as? String) ?: "",
                    myGrid = (values[1] as? String) ?: "",
                    theme = (values[2] as? String) ?: "system",
                    defaultPower = (values[3] as? Int)?.toString() ?: "",
                    defaultMode = (values[4] as? String) ?: "",
                    defaultBand = (values[5] as? String) ?: "",
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateCallsign(callsign: String) {
        _uiState.value = _uiState.value.copy(myCallsign = callsign.uppercase())
    }

    fun updateGrid(grid: String) {
        _uiState.value = _uiState.value.copy(myGrid = grid.uppercase())
    }

    fun updateTheme(theme: String) {
        _uiState.value = _uiState.value.copy(theme = theme)
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun updateDefaultPower(power: String) {
        _uiState.value = _uiState.value.copy(defaultPower = power)
    }

    fun updateDefaultMode(mode: String) {
        _uiState.value = _uiState.value.copy(defaultMode = mode)
        viewModelScope.launch {
            settingsRepository.setDefaultMode(mode)
        }
    }

    fun updateDefaultBand(band: String) {
        _uiState.value = _uiState.value.copy(defaultBand = band)
        viewModelScope.launch {
            settingsRepository.setDefaultBand(band)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                val state = _uiState.value

                if (state.myCallsign.isNotBlank()) {
                    settingsRepository.setMyCallsign(state.myCallsign)
                }

                if (state.myGrid.isNotBlank()) {
                    settingsRepository.setMyGrid(state.myGrid)
                }

                state.defaultPower.toIntOrNull()?.let {
                    settingsRepository.setDefaultPower(it)
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Settings saved"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Error saving settings: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun clearAllData() {
        viewModelScope.launch {
            settingsRepository.clearAllSettings()
            _uiState.value = SettingsUiState(isLoading = false, message = "All settings cleared")
        }
    }
}
