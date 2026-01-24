package com.hamhub.app.ui.screens.spotter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.local.database.dao.SpotterDao
import com.hamhub.app.data.local.database.dao.SpotterListWithCount
import com.hamhub.app.data.local.entity.SpotterListEntity
import com.hamhub.app.data.local.entity.SpottedCallsignEntity
import com.hamhub.app.data.remote.dto.CallookResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpotterUiState(
    val lists: List<SpotterListWithCount> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateListDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val listToDelete: SpotterListWithCount? = null,
    val error: String? = null
)

data class SpotterListDetailUiState(
    val list: SpotterListEntity? = null,
    val callsigns: List<SpottedCallsignEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SpotterViewModel @Inject constructor(
    private val spotterDao: SpotterDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotterUiState())
    val uiState: StateFlow<SpotterUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(SpotterListDetailUiState())
    val detailUiState: StateFlow<SpotterListDetailUiState> = _detailUiState.asStateFlow()

    init {
        loadLists()
    }

    private fun loadLists() {
        viewModelScope.launch {
            spotterDao.getListsWithCount().collect { lists ->
                _uiState.value = _uiState.value.copy(
                    lists = lists,
                    isLoading = false
                )
            }
        }
    }

    fun loadListDetail(listId: Long) {
        viewModelScope.launch {
            _detailUiState.value = _detailUiState.value.copy(isLoading = true)

            val list = spotterDao.getListById(listId)
            _detailUiState.value = _detailUiState.value.copy(list = list)

            spotterDao.getCallsignsForList(listId).collect { callsigns ->
                _detailUiState.value = _detailUiState.value.copy(
                    callsigns = callsigns,
                    isLoading = false
                )
            }
        }
    }

    fun showCreateListDialog() {
        _uiState.value = _uiState.value.copy(showCreateListDialog = true)
    }

    fun hideCreateListDialog() {
        _uiState.value = _uiState.value.copy(showCreateListDialog = false)
    }

    fun createList(name: String, description: String? = null) {
        viewModelScope.launch {
            val list = SpotterListEntity(
                name = name.trim(),
                description = description?.trim()?.takeIf { it.isNotEmpty() }
            )
            spotterDao.insertList(list)
            hideCreateListDialog()
        }
    }

    fun showDeleteConfirmDialog(list: SpotterListWithCount) {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmDialog = true,
            listToDelete = list
        )
    }

    fun hideDeleteConfirmDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmDialog = false,
            listToDelete = null
        )
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch {
            spotterDao.deleteList(listId)
            hideDeleteConfirmDialog()
        }
    }

    fun removeCallsignFromList(callsignId: Long) {
        viewModelScope.launch {
            spotterDao.deleteCallsign(callsignId)
        }
    }

    // Methods for saving callsigns from lookup
    fun saveCallsignToList(listId: Long, response: CallookResponse) {
        viewModelScope.launch {
            val callsign = response.current?.callsign ?: return@launch

            // Check if already in list
            if (spotterDao.isCallsignInList(listId, callsign)) {
                return@launch
            }

            val location = buildString {
                response.address?.let { address ->
                    if (address.line2.isNotBlank()) {
                        append(address.line2)
                    }
                }
            }

            val spottedCallsign = SpottedCallsignEntity(
                listId = listId,
                callsign = callsign,
                name = response.name.takeIf { it.isNotBlank() },
                gridSquare = response.location?.gridsquare?.takeIf { it.isNotBlank() },
                location = location.takeIf { it.isNotBlank() },
                operatorClass = response.current?.operClass?.takeIf { it.isNotBlank() }
            )

            spotterDao.insertCallsign(spottedCallsign)
        }
    }

    fun getListsFlow() = spotterDao.getAllLists()
}
