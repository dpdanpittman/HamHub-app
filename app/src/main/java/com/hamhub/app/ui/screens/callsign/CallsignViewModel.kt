package com.hamhub.app.ui.screens.callsign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.local.database.dao.SettingsDao
import com.hamhub.app.data.local.database.dao.SpotterDao
import com.hamhub.app.data.local.entity.SpotterListEntity
import com.hamhub.app.data.local.entity.SpottedCallsignEntity
import com.hamhub.app.data.remote.api.CallookApi
import com.hamhub.app.data.remote.dto.CallookResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecentSearch(
    val callsign: String,
    val name: String,
    val timestamp: Long
)

data class CallsignUiState(
    val query: String = "",
    val result: CallookResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val recentSearches: List<RecentSearch> = emptyList(),
    val showAllRecentSearches: Boolean = false,
    val showSaveToListDialog: Boolean = false,
    val spotterLists: List<SpotterListEntity> = emptyList(),
    val saveSuccess: String? = null
)

@HiltViewModel
class CallsignViewModel @Inject constructor(
    private val callookApi: CallookApi,
    private val settingsDao: SettingsDao,
    private val spotterDao: SpotterDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallsignUiState())
    val uiState: StateFlow<CallsignUiState> = _uiState.asStateFlow()

    companion object {
        private const val MAX_RECENT_SEARCHES = 20
        private const val VISIBLE_RECENT_SEARCHES = 6
    }

    init {
        loadRecentSearches()
        loadSpotterLists()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            val searchesJson = settingsDao.getSettingValue(SettingsDao.KEY_RECENT_CALLSIGN_SEARCHES)
            val searches = parseRecentSearches(searchesJson)
            _uiState.value = _uiState.value.copy(recentSearches = searches)
        }
    }

    private fun loadSpotterLists() {
        viewModelScope.launch {
            spotterDao.getAllLists().collect { lists ->
                _uiState.value = _uiState.value.copy(spotterLists = lists)
            }
        }
    }

    private fun parseRecentSearches(json: String?): List<RecentSearch> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            // Simple format: "CALLSIGN|NAME|TIMESTAMP;CALLSIGN|NAME|TIMESTAMP;..."
            json.split(";").mapNotNull { entry ->
                val parts = entry.split("|")
                if (parts.size >= 3) {
                    RecentSearch(
                        callsign = parts[0],
                        name = parts[1],
                        timestamp = parts[2].toLongOrNull() ?: 0L
                    )
                } else null
            }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeRecentSearches(searches: List<RecentSearch>): String {
        return searches.joinToString(";") { "${it.callsign}|${it.name}|${it.timestamp}" }
    }

    private suspend fun saveRecentSearch(callsign: String, name: String) {
        val currentSearches = _uiState.value.recentSearches.toMutableList()

        // Remove existing entry for this callsign if present
        currentSearches.removeAll { it.callsign.equals(callsign, ignoreCase = true) }

        // Add new entry at the beginning
        currentSearches.add(0, RecentSearch(callsign, name, System.currentTimeMillis()))

        // Keep only the most recent entries
        val trimmedSearches = currentSearches.take(MAX_RECENT_SEARCHES)

        // Save to database
        settingsDao.setSetting(
            SettingsDao.KEY_RECENT_CALLSIGN_SEARCHES,
            serializeRecentSearches(trimmedSearches)
        )

        _uiState.value = _uiState.value.copy(recentSearches = trimmedSearches)
    }

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
                    // Save to recent searches
                    saveRecentSearch(query, result.name)
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

    fun searchCallsign(callsign: String) {
        _uiState.value = _uiState.value.copy(query = callsign)
        search()
    }

    fun toggleShowAllRecentSearches() {
        _uiState.value = _uiState.value.copy(
            showAllRecentSearches = !_uiState.value.showAllRecentSearches
        )
    }

    fun getVisibleRecentSearches(): List<RecentSearch> {
        val all = _uiState.value.recentSearches
        return if (_uiState.value.showAllRecentSearches) all else all.take(VISIBLE_RECENT_SEARCHES)
    }

    fun hasMoreRecentSearches(): Boolean {
        return _uiState.value.recentSearches.size > VISIBLE_RECENT_SEARCHES
    }

    fun clear() {
        _uiState.value = _uiState.value.copy(
            query = "",
            result = null,
            error = null,
            hasSearched = false,
            showAllRecentSearches = false
        )
    }

    // Spotter list functions
    fun showSaveToListDialog() {
        _uiState.value = _uiState.value.copy(showSaveToListDialog = true)
    }

    fun hideSaveToListDialog() {
        _uiState.value = _uiState.value.copy(showSaveToListDialog = false)
    }

    fun saveToList(listId: Long) {
        val result = _uiState.value.result ?: return
        val callsign = result.current?.callsign ?: return

        viewModelScope.launch {
            // Check if already in list
            if (spotterDao.isCallsignInList(listId, callsign)) {
                _uiState.value = _uiState.value.copy(
                    showSaveToListDialog = false,
                    saveSuccess = "$callsign is already in this list"
                )
                return@launch
            }

            val location = buildString {
                result.address?.let { address ->
                    if (address.line2.isNotBlank()) {
                        append(address.line2)
                    }
                }
            }

            val spottedCallsign = SpottedCallsignEntity(
                listId = listId,
                callsign = callsign,
                name = result.name.takeIf { it.isNotBlank() },
                gridSquare = result.location?.gridsquare?.takeIf { it.isNotBlank() },
                location = location.takeIf { it.isNotBlank() },
                operatorClass = result.current?.operClass?.takeIf { it.isNotBlank() }
            )

            spotterDao.insertCallsign(spottedCallsign)

            val listName = spotterDao.getListById(listId)?.name ?: "list"
            _uiState.value = _uiState.value.copy(
                showSaveToListDialog = false,
                saveSuccess = "$callsign saved to $listName"
            )
        }
    }

    fun createListAndSave(listName: String) {
        val result = _uiState.value.result ?: return
        val callsign = result.current?.callsign ?: return

        viewModelScope.launch {
            // Create the new list
            val list = SpotterListEntity(name = listName.trim())
            val listId = spotterDao.insertList(list)

            // Save the callsign to the new list
            val location = buildString {
                result.address?.let { address ->
                    if (address.line2.isNotBlank()) {
                        append(address.line2)
                    }
                }
            }

            val spottedCallsign = SpottedCallsignEntity(
                listId = listId,
                callsign = callsign,
                name = result.name.takeIf { it.isNotBlank() },
                gridSquare = result.location?.gridsquare?.takeIf { it.isNotBlank() },
                location = location.takeIf { it.isNotBlank() },
                operatorClass = result.current?.operClass?.takeIf { it.isNotBlank() }
            )

            spotterDao.insertCallsign(spottedCallsign)

            _uiState.value = _uiState.value.copy(
                showSaveToListDialog = false,
                saveSuccess = "$callsign saved to $listName"
            )
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = null)
    }
}
