package com.hamhub.app.data.repository

import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.entity.QsoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DashboardStats(
    val totalQsos: Int = 0,
    val uniqueCallsigns: Int = 0,
    val uniqueCountries: Int = 0,
    val uniqueGrids: Int = 0,
    val uniqueStates: Int = 0,
    val qsosByBand: Map<String, Int> = emptyMap(),
    val qsosByMode: Map<String, Int> = emptyMap(),
    val qsosByMonth: Map<String, Int> = emptyMap(),
    val qsosByHour: Map<Int, Int> = emptyMap(),
    val recentQsos: List<QsoEntity> = emptyList()
)

@Singleton
class StatsRepository @Inject constructor(
    private val qsoDao: QsoDao
) {
    fun getDashboardStats(): Flow<DashboardStats> {
        return qsoDao.getAllQsos().map { qsos ->
            calculateStats(qsos)
        }
    }

    private fun calculateStats(qsos: List<QsoEntity>): DashboardStats {
        if (qsos.isEmpty()) {
            return DashboardStats()
        }

        // Basic counts
        val uniqueCallsigns = qsos.map { it.callsign.uppercase() }.toSet().size
        val uniqueCountries = qsos.mapNotNull { it.country }.toSet().size
        val uniqueGrids = qsos.mapNotNull { it.gridSquare?.take(4)?.uppercase() }.toSet().size
        val uniqueStates = qsos.mapNotNull { it.state?.uppercase() }.toSet().size

        // QSOs by band
        val qsosByBand = qsos
            .groupBy { it.band }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .toMap()

        // QSOs by mode
        val qsosByMode = qsos
            .groupBy { it.mode }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .toMap()

        // QSOs by month (last 12 months)
        val qsosByMonth = qsos
            .groupBy { it.date.substring(0, 7) } // YYYY-MM
            .mapValues { it.value.size }
            .toList()
            .sortedBy { it.first }
            .takeLast(12)
            .toMap()

        // QSOs by hour of day
        val qsosByHour = qsos
            .mapNotNull {
                try {
                    it.timeUtc.substring(0, 2).toInt()
                } catch (e: Exception) {
                    null
                }
            }
            .groupBy { it }
            .mapValues { it.value.size }

        // Recent QSOs (last 5)
        val recentQsos = qsos.take(5)

        return DashboardStats(
            totalQsos = qsos.size,
            uniqueCallsigns = uniqueCallsigns,
            uniqueCountries = uniqueCountries,
            uniqueGrids = uniqueGrids,
            uniqueStates = uniqueStates,
            qsosByBand = qsosByBand,
            qsosByMode = qsosByMode,
            qsosByMonth = qsosByMonth,
            qsosByHour = qsosByHour,
            recentQsos = recentQsos
        )
    }

    suspend fun getTotalQsoCount(): Int = qsoDao.getQsoCount()

    suspend fun getUniqueCallsignCount(): Int = qsoDao.getUniqueCallsignCount()

    suspend fun getUniqueCountryCount(): Int = qsoDao.getUniqueCountryCount()

    suspend fun getUniqueGridCount(): Int = qsoDao.getUniqueGridCount()
}
