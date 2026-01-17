package com.hamhub.app.ui.screens.awards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.repository.AwardsOverview
import com.hamhub.app.data.repository.AwardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AwardsUiState(
    val awards: AwardsOverview = AwardsOverview(),
    val isLoading: Boolean = true,
    val selectedTab: Int = 0
)

@HiltViewModel
class AwardsViewModel @Inject constructor(
    private val awardsRepository: AwardsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AwardsUiState())
    val uiState: StateFlow<AwardsUiState> = _uiState.asStateFlow()

    init {
        loadAwards()
    }

    private fun loadAwards() {
        viewModelScope.launch {
            awardsRepository.getAwardsOverview().collect { awards ->
                _uiState.value = AwardsUiState(
                    awards = awards,
                    isLoading = false,
                    selectedTab = _uiState.value.selectedTab
                )
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }
}
