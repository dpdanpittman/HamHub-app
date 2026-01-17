package com.hamhub.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.local.entity.QsoEntity
import com.hamhub.app.data.repository.QsoRepository
import com.hamhub.app.domain.util.GridSquareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapMarker(
    val id: Long,
    val callsign: String,
    val gridSquare: String,
    val latitude: Double,
    val longitude: Double,
    val band: String,
    val mode: String,
    val date: String,
    val qsoCount: Int = 1
)

data class MapUiState(
    val markers: List<MapMarker> = emptyList(),
    val clusteredMarkers: List<MapMarker> = emptyList(),
    val isLoading: Boolean = true,
    val selectedMarker: MapMarker? = null,
    val totalQsosWithGrid: Int = 0,
    val uniqueGrids: Int = 0
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val qsoRepository: QsoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMapData()
    }

    private fun loadMapData() {
        viewModelScope.launch {
            qsoRepository.getAllQsos().collect { qsos ->
                val markers = qsosToMarkers(qsos)
                val clustered = clusterMarkers(markers)

                _uiState.value = MapUiState(
                    markers = markers,
                    clusteredMarkers = clustered,
                    isLoading = false,
                    totalQsosWithGrid = markers.size,
                    uniqueGrids = markers.map { it.gridSquare.take(4) }.toSet().size
                )
            }
        }
    }

    private fun qsosToMarkers(qsos: List<QsoEntity>): List<MapMarker> {
        return qsos
            .filter { it.gridSquare != null && GridSquareUtils.isValidGrid(it.gridSquare) }
            .mapNotNull { qso ->
                qso.gridSquare?.let { grid ->
                    GridSquareUtils.gridToLatLng(grid)?.let { latLng ->
                        MapMarker(
                            id = qso.id,
                            callsign = qso.callsign,
                            gridSquare = grid,
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            band = qso.band,
                            mode = qso.mode,
                            date = qso.date
                        )
                    }
                }
            }
    }

    /**
     * Cluster markers that are in the same 4-character grid square.
     */
    private fun clusterMarkers(markers: List<MapMarker>): List<MapMarker> {
        return markers
            .groupBy { it.gridSquare.take(4).uppercase() }
            .map { (grid, groupedMarkers) ->
                if (groupedMarkers.size == 1) {
                    groupedMarkers.first()
                } else {
                    // Create a cluster marker at the center of the grid
                    val latLng = GridSquareUtils.gridToLatLng(grid)
                    val firstMarker = groupedMarkers.first()
                    MapMarker(
                        id = -grid.hashCode().toLong(),
                        callsign = "${groupedMarkers.size} QSOs",
                        gridSquare = grid,
                        latitude = latLng?.latitude ?: firstMarker.latitude,
                        longitude = latLng?.longitude ?: firstMarker.longitude,
                        band = groupedMarkers.map { it.band }.distinct().joinToString(", "),
                        mode = groupedMarkers.map { it.mode }.distinct().joinToString(", "),
                        date = groupedMarkers.maxOf { it.date },
                        qsoCount = groupedMarkers.size
                    )
                }
            }
    }

    fun selectMarker(marker: MapMarker?) {
        _uiState.value = _uiState.value.copy(selectedMarker = marker)
    }

    fun dismissMarkerDetails() {
        _uiState.value = _uiState.value.copy(selectedMarker = null)
    }
}
