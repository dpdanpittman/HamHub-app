package com.hamhub.app.ui.screens.callsign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.api.CallookApi
import com.hamhub.app.data.remote.dto.CallookResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CallsignUiState(
    val query: String = "",
    val result: CallookResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class CallsignViewModel @Inject constructor(
    private val callookApi: CallookApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallsignUiState())
    val uiState: StateFlow<CallsignUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun search() {
        val query = _uiState.value.query.trim().uppercase()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                hasSearched = true
            )

            try {
                val result = callookApi.lookupCallsign(query)

                if (result.status == "VALID") {
                    _uiState.value = _uiState.value.copy(
                        result = result,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        result = null,
                        isLoading = false,
                        error = "Callsign not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    result = null,
                    isLoading = false,
                    error = e.message ?: "Lookup failed"
                )
            }
        }
    }

    fun clear() {
        _uiState.value = CallsignUiState()
    }
}
