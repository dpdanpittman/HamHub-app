package com.hamhub.app.data.repository

import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.domain.model.UsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DxccProgress(
    val totalWorked: Int = 0,
    val totalConfirmed: Int = 0,
    val byBand: Map<String, BandDxcc> = emptyMap(),
    val byMode: Map<String, ModeDxcc> = emptyMap(),
    val countriesWorked: Set<String> = emptySet()
)

data class BandDxcc(
    val band: String,
    val worked: Int,
    val confirmed: Int
)

data class ModeDxcc(
    val mode: String,
    val worked: Int,
    val confirmed: Int
)

data class WasProgress(
    val statesWorked: Set<String> = emptySet(),
    val statesConfirmed: Set<String> = emptySet(),
    val statesMissing: Set<String> = UsState.allCodes.toSet(),
    val totalWorked: Int = 0,
    val totalConfirmed: Int = 0,
    val percentComplete: Float = 0f
)

data class GridProgress(
    val totalGridsWorked: Int = 0,
    val totalGridsConfirmed: Int = 0,
    val gridsWorked: Set<String> = emptySet(),
    val gridsByBand: Map<String, Int> = emptyMap()
)

data class AwardsOverview(
    val dxcc: DxccProgress = DxccProgress(),
    val was: WasProgress = WasProgress(),
    val grids: GridProgress = GridProgress()
)

@Singleton
class AwardsRepository @Inject constructor(
    private val qsoDao: QsoDao
) {
    fun getAwardsOverview(): Flow<AwardsOverview> {
        return qsoDao.getAllQsos().map { qsos ->
            AwardsOverview(
                dxcc = calculateDxccProgress(qsos),
                was = calculateWasProgress(qsos),
                grids = calculateGridProgress(qsos)
            )
        }
    }

    fun getDxccProgress(): Flow<DxccProgress> {
        return qsoDao.getAllQsos().map { qsos ->
            calculateDxccProgress(qsos)
        }
    }

    fun getWasProgress(): Flow<WasProgress> {
        return qsoDao.getAllQsos().map { qsos ->
            calculateWasProgress(qsos)
        }
    }

    fun getGridProgress(): Flow<GridProgress> {
        return qsoDao.getAllQsos().map { qsos ->
            calculateGridProgress(qsos)
        }
    }

    private fun calculateDxccProgress(qsos: List<com.hamhub.app.data.local.database.entity.QsoEntity>): DxccProgress {
        val countriesWorked = qsos.mapNotNull { it.country?.uppercase() }.toSet()
        val countriesConfirmed = qsos.filter { it.qslReceived }.mapNotNull { it.country?.uppercase() }.toSet()

        // By band
        val byBand = qsos
            .filter { it.country != null }
            .groupBy { it.band }
            .mapValues { (band, bandQsos) ->
                val worked = bandQsos.mapNotNull { it.country?.uppercase() }.toSet().size
                val confirmed = bandQsos.filter { it.qslReceived }.mapNotNull { it.country?.uppercase() }.toSet().size
                BandDxcc(band, worked, confirmed)
            }

        // By mode
        val byMode = qsos
            .filter { it.country != null }
            .groupBy { it.mode }
            .mapValues { (mode, modeQsos) ->
                val worked = modeQsos.mapNotNull { it.country?.uppercase() }.toSet().size
                val confirmed = modeQsos.filter { it.qslReceived }.mapNotNull { it.country?.uppercase() }.toSet().size
                ModeDxcc(mode, worked, confirmed)
            }

        return DxccProgress(
            totalWorked = countriesWorked.size,
            totalConfirmed = countriesConfirmed.size,
            byBand = byBand,
            byMode = byMode,
            countriesWorked = countriesWorked
        )
    }

    private fun calculateWasProgress(qsos: List<com.hamhub.app.data.local.database.entity.QsoEntity>): WasProgress {
        val statesWorked = qsos
            .mapNotNull { it.state?.uppercase() }
            .filter { UsState.fromCode(it) != null }
            .toSet()

        val statesConfirmed = qsos
            .filter { it.qslReceived }
            .mapNotNull { it.state?.uppercase() }
            .filter { UsState.fromCode(it) != null }
            .toSet()

        val statesMissing = UsState.allCodes.toSet() - statesWorked

        return WasProgress(
            statesWorked = statesWorked,
            statesConfirmed = statesConfirmed,
            statesMissing = statesMissing,
            totalWorked = statesWorked.size,
            totalConfirmed = statesConfirmed.size,
            percentComplete = (statesWorked.size / 50f) * 100f
        )
    }

    private fun calculateGridProgress(qsos: List<com.hamhub.app.data.local.database.entity.QsoEntity>): GridProgress {
        // Use 4-character grid (e.g., "FN31")
        val gridsWorked = qsos
            .mapNotNull { it.gridSquare?.take(4)?.uppercase() }
            .filter { it.length == 4 }
            .toSet()

        val gridsConfirmed = qsos
            .filter { it.qslReceived }
            .mapNotNull { it.gridSquare?.take(4)?.uppercase() }
            .filter { it.length == 4 }
            .toSet()

        val gridsByBand = qsos
            .filter { it.gridSquare != null && it.gridSquare.length >= 4 }
            .groupBy { it.band }
            .mapValues { (_, bandQsos) ->
                bandQsos.mapNotNull { it.gridSquare?.take(4)?.uppercase() }.toSet().size
            }

        return GridProgress(
            totalGridsWorked = gridsWorked.size,
            totalGridsConfirmed = gridsConfirmed.size,
            gridsWorked = gridsWorked,
            gridsByBand = gridsByBand
        )
    }
}
