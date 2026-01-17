package com.hamhub.app.ui.screens.propagation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.dto.PropagationData
import com.hamhub.app.data.repository.PropagationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropagationUiState(
    val data: PropagationData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PropagationViewModel @Inject constructor(
    private val repository: PropagationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropagationUiState())
    val uiState: StateFlow<PropagationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getPropagationData(forceRefresh).fold(
                onSuccess = { data ->
                    _uiState.value = PropagationUiState(data = data, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = PropagationUiState(
                        isLoading = false,
                        error = e.message ?: "Failed to load propagation data"
                    )
                }
            )
        }
    }

    fun refresh() = loadData(forceRefresh = true)
}
