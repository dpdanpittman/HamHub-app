package com.hamhub.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<String> = settingsRepository.getThemeFlow()
        .map { it ?: "system" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )
}
