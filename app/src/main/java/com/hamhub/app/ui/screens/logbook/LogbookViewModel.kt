package com.hamhub.app.ui.screens.logbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.local.entity.QsoEntity
import com.hamhub.app.data.repository.QsoRepository
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class LogbookUiState(
    val qsos: List<QsoEntity> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedBandFilter: String? = null,
    val selectedModeFilter: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val qsoToDelete: QsoEntity? = null,
    val duplicateWarning: QsoEntity? = null
)

data class QsoFormState(
    val date: String = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE),
    val timeUtc: String = LocalTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm")),
    val callsign: String = "",
    val frequency: String = "",
    val band: String = Band.BAND_20M.display,
    val mode: String = Mode.SSB.display,
    val rstSent: String = "59",
    val rstReceived: String = "59",
    val name: String = "",
    val qth: String = "",
    val gridSquare: String = "",
    val country: String = "",
    val state: String = "",
    val power: String = "",
    val notes: String = "",
    val qslSent: Boolean = false,
    val qslReceived: Boolean = false
) {
    val isValid: Boolean
        get() = callsign.isNotBlank() && band.isNotBlank() && mode.isNotBlank()

    fun toEntity(existingId: Long? = null): QsoEntity {
        return QsoEntity(
            id = existingId ?: 0,
            date = date,
            timeUtc = timeUtc,
            callsign = callsign.uppercase().trim(),
            frequency = frequency.toDoubleOrNull(),
            band = band,
            mode = mode,
            rstSent = rstSent.ifBlank { null },
            rstReceived = rstReceived.ifBlank { null },
            name = name.ifBlank { null },
            qth = qth.ifBlank { null },
            gridSquare = gridSquare.uppercase().ifBlank { null },
            country = country.ifBlank { null },
            dxcc = null,
            state = state.uppercase().ifBlank { null },
            power = power.toIntOrNull(),
            notes = notes.ifBlank { null },
            qslSent = qslSent,
            qslReceived = qslReceived,
            contestName = null,
            contestExchange = null,
            updatedAt = System.currentTimeMillis()
        )
    }

    companion object {
        fun fromEntity(entity: QsoEntity): QsoFormState {
            return QsoFormState(
                date = entity.date,
                timeUtc = entity.timeUtc,
                callsign = entity.callsign,
                frequency = entity.frequency?.toString() ?: "",
                band = entity.band,
                mode = entity.mode,
                rstSent = entity.rstSent ?: "59",
                rstReceived = entity.rstReceived ?: "59",
                name = entity.name ?: "",
                qth = entity.qth ?: "",
                gridSquare = entity.gridSquare ?: "",
                country = entity.country ?: "",
                state = entity.state ?: "",
                power = entity.power?.toString() ?: "",
                notes = entity.notes ?: "",
                qslSent = entity.qslSent,
                qslReceived = entity.qslReceived
            )
        }
    }
}

@HiltViewModel
class LogbookViewModel @Inject constructor(
    private val qsoRepository: QsoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(QsoFormState())
    val formState: StateFlow<QsoFormState> = _formState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val bandFilter = MutableStateFlow<String?>(null)
    private val modeFilter = MutableStateFlow<String?>(null)

    init {
        loadQsos()
    }

    private fun loadQsos() {
        viewModelScope.launch {
            combine(
                qsoRepository.getAllQsos(),
                searchQuery,
                bandFilter,
                modeFilter
            ) { allQsos, query, band, mode ->
                var filtered = allQsos

                // Apply search filter
                if (query.isNotBlank()) {
                    filtered = filtered.filter {
                        it.callsign.contains(query, ignoreCase = true) ||
                        it.name?.contains(query, ignoreCase = true) == true ||
                        it.notes?.contains(query, ignoreCase = true) == true
                    }
                }

                // Apply band filter
                if (band != null) {
                    filtered = filtered.filter { it.band == band }
                }

                // Apply mode filter
                if (mode != null) {
                    filtered = filtered.filter { it.mode == mode }
                }

                filtered
            }.collect { qsos ->
                _uiState.update {
                    it.copy(
                        qsos = qsos,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onBandFilterChange(band: String?) {
        bandFilter.value = band
        _uiState.update { it.copy(selectedBandFilter = band) }
    }

    fun onModeFilterChange(mode: String?) {
        modeFilter.value = mode
        _uiState.update { it.copy(selectedModeFilter = mode) }
    }

    fun clearFilters() {
        searchQuery.value = ""
        bandFilter.value = null
        modeFilter.value = null
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedBandFilter = null,
                selectedModeFilter = null
            )
        }
    }

    fun initNewQso() {
        _formState.value = QsoFormState()
        _uiState.update { it.copy(duplicateWarning = null) }
    }

    fun loadQsoForEdit(qsoId: Long) {
        viewModelScope.launch {
            qsoRepository.getQsoById(qsoId)?.let { qso ->
                _formState.value = QsoFormState.fromEntity(qso)
            }
        }
    }

    fun updateFormField(update: QsoFormState.() -> QsoFormState) {
        _formState.update { it.update() }
    }

    suspend fun saveQso(existingId: Long? = null): Boolean {
        val currentForm = _formState.value

        // Check for duplicate (only for new QSOs)
        if (existingId == null) {
            val duplicate = qsoRepository.findDuplicate(
                callsign = currentForm.callsign.uppercase().trim(),
                date = currentForm.date,
                band = currentForm.band,
                mode = currentForm.mode
            )
            if (duplicate != null) {
                _uiState.update { it.copy(duplicateWarning = duplicate) }
                return false
            }
        }

        val entity = currentForm.toEntity(existingId)

        if (existingId != null) {
            qsoRepository.updateQso(entity)
        } else {
            qsoRepository.insertQso(entity)
        }

        return true
    }

    suspend fun saveQsoIgnoringDuplicate(existingId: Long? = null) {
        val currentForm = _formState.value
        val entity = currentForm.toEntity(existingId)

        if (existingId != null) {
            qsoRepository.updateQso(entity)
        } else {
            qsoRepository.insertQso(entity)
        }
    }

    fun dismissDuplicateWarning() {
        _uiState.update { it.copy(duplicateWarning = null) }
    }

    fun showDeleteConfirmation(qso: QsoEntity) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                qsoToDelete = qso
            )
        }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                qsoToDelete = null
            )
        }
    }

    fun deleteQso() {
        viewModelScope.launch {
            _uiState.value.qsoToDelete?.let { qso ->
                qsoRepository.deleteQso(qso)
            }
            dismissDeleteConfirmation()
        }
    }
}
