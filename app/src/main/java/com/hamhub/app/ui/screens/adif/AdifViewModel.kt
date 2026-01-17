package com.hamhub.app.ui.screens.adif

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.domain.usecase.ExportAdifUseCase
import com.hamhub.app.domain.usecase.ExportResult
import com.hamhub.app.domain.usecase.ImportAdifUseCase
import com.hamhub.app.domain.usecase.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

data class AdifUiState(
    val isImporting: Boolean = false,
    val isExporting: Boolean = false,
    val importResult: ImportResult? = null,
    val exportResult: ExportResult? = null,
    val error: String? = null,
    val showExportOptions: Boolean = false,
    val bandFilter: String? = null,
    val modeFilter: String? = null
)

@HiltViewModel
class AdifViewModel @Inject constructor(
    private val importAdifUseCase: ImportAdifUseCase,
    private val exportAdifUseCase: ExportAdifUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdifUiState())
    val uiState: StateFlow<AdifUiState> = _uiState.asStateFlow()

    fun importFromUri(context: Context, uri: Uri, skipDuplicates: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, error = null, importResult = null) }

            try {
                val content = readFileFromUri(context, uri)
                val result = importAdifUseCase(content, skipDuplicates)
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        importResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        error = "Failed to import: ${e.message}"
                    )
                }
            }
        }
    }

    fun exportToString(): String? {
        var result: ExportResult? = null
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null, exportResult = null) }

            try {
                result = exportAdifUseCase(
                    bandFilter = _uiState.value.bandFilter,
                    modeFilter = _uiState.value.modeFilter
                )
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        error = "Failed to export: ${e.message}"
                    )
                }
            }
        }
        return result?.content
    }

    suspend fun getExportContent(): ExportResult {
        return exportAdifUseCase(
            bandFilter = _uiState.value.bandFilter,
            modeFilter = _uiState.value.modeFilter
        )
    }

    fun showExportOptions() {
        _uiState.update { it.copy(showExportOptions = true) }
    }

    fun hideExportOptions() {
        _uiState.update { it.copy(showExportOptions = false) }
    }

    fun setBandFilter(band: String?) {
        _uiState.update { it.copy(bandFilter = band) }
    }

    fun setModeFilter(mode: String?) {
        _uiState.update { it.copy(modeFilter = mode) }
    }

    fun clearFilters() {
        _uiState.update { it.copy(bandFilter = null, modeFilter = null) }
    }

    fun dismissResult() {
        _uiState.update { it.copy(importResult = null, exportResult = null, error = null) }
    }

    private fun readFileFromUri(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Could not open file")

        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.readText()
        }
    }
}
