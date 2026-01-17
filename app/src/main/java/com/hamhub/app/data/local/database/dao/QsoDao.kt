package com.hamhub.app.data.local.database.dao

import androidx.room.*
import com.hamhub.app.data.local.entity.QsoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QsoDao {

    @Query("SELECT * FROM qsos ORDER BY date DESC, time_utc DESC")
    fun getAllQsos(): Flow<List<QsoEntity>>

    @Query("SELECT * FROM qsos WHERE id = :id")
    suspend fun getQsoById(id: Long): QsoEntity?

    @Query("""
        SELECT * FROM qsos
        WHERE (:band IS NULL OR band = :band)
        AND (:mode IS NULL OR mode = :mode)
        AND (:callsign IS NULL OR callsign LIKE '%' || :callsign || '%')
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY date DESC, time_utc DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getQsosFiltered(
        band: String? = null,
        mode: String? = null,
        callsign: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<QsoEntity>

    @Query("SELECT COUNT(*) FROM qsos")
    suspend fun getQsoCount(): Int

    @Query("SELECT COUNT(*) FROM qsos")
    fun getQsoCountFlow(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT callsign) FROM qsos")
    suspend fun getUniqueCallsignCount(): Int

    @Query("SELECT COUNT(DISTINCT country) FROM qsos WHERE country IS NOT NULL")
    suspend fun getUniqueCountryCount(): Int

    @Query("SELECT COUNT(DISTINCT grid_square) FROM qsos WHERE grid_square IS NOT NULL")
    suspend fun getUniqueGridCount(): Int

    @Query("SELECT COUNT(*) FROM qsos WHERE qsl_received = 1")
    suspend fun getConfirmedQsoCount(): Int

    // Statistics queries
    @Query("SELECT band, COUNT(*) as count FROM qsos GROUP BY band ORDER BY count DESC")
    suspend fun getQsosByBand(): List<BandCount>

    @Query("SELECT mode, COUNT(*) as count FROM qsos GROUP BY mode ORDER BY count DESC")
    suspend fun getQsosByMode(): List<ModeCount>

    @Query("""
        SELECT date, COUNT(*) as count
        FROM qsos
        WHERE date >= :startDate
        GROUP BY date
        ORDER BY date
    """)
    suspend fun getQsosByDate(startDate: String): List<DateCount>

    @Query("""
        SELECT CAST(SUBSTR(time_utc, 1, 2) AS INTEGER) as hour, COUNT(*) as count
        FROM qsos
        GROUP BY hour
        ORDER BY hour
    """)
    suspend fun getQsosByHour(): List<HourCount>

    // Country/DXCC queries
    @Query("""
        SELECT country, dxcc,
               COUNT(*) as worked,
               SUM(CASE WHEN qsl_received = 1 THEN 1 ELSE 0 END) as confirmed
        FROM qsos
        WHERE country IS NOT NULL
        GROUP BY country, dxcc
        ORDER BY country
    """)
    suspend fun getCountryStats(): List<CountryStats>

    // State queries (for WAS)
    @Query("""
        SELECT state,
               COUNT(*) as worked,
               SUM(CASE WHEN qsl_received = 1 THEN 1 ELSE 0 END) as confirmed
        FROM qsos
        WHERE state IS NOT NULL AND country = 'United States'
        GROUP BY state
        ORDER BY state
    """)
    suspend fun getStateStats(): List<StateStats>

    // Grid square queries
    @Query("""
        SELECT grid_square AS gridSquare,
               COUNT(*) as worked,
               SUM(CASE WHEN qsl_received = 1 THEN 1 ELSE 0 END) as confirmed
        FROM qsos
        WHERE grid_square IS NOT NULL
        GROUP BY grid_square
        ORDER BY grid_square
    """)
    suspend fun getGridStats(): List<GridStats>

    // Duplicate check
    @Query("""
        SELECT * FROM qsos
        WHERE callsign = :callsign
        AND date = :date
        AND band = :band
        AND mode = :mode
        LIMIT 1
    """)
    suspend fun findDuplicate(
        callsign: String,
        date: String,
        band: String,
        mode: String
    ): QsoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQso(qso: QsoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQsos(qsos: List<QsoEntity>): List<Long>

    @Update
    suspend fun updateQso(qso: QsoEntity)

    @Delete
    suspend fun deleteQso(qso: QsoEntity)

    @Query("DELETE FROM qsos WHERE id = :id")
    suspend fun deleteQsoById(id: Long)

    @Query("DELETE FROM qsos")
    suspend fun deleteAllQsos()

    // Get all for export
    @Query("""
        SELECT * FROM qsos
        WHERE (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND (:band IS NULL OR band = :band)
        AND (:mode IS NULL OR mode = :mode)
        ORDER BY date, time_utc
    """)
    suspend fun getQsosForExport(
        startDate: String? = null,
        endDate: String? = null,
        band: String? = null,
        mode: String? = null
    ): List<QsoEntity>
}

// Data classes for aggregate queries
data class BandCount(val band: String, val count: Int)
data class ModeCount(val mode: String, val count: Int)
data class DateCount(val date: String, val count: Int)
data class HourCount(val hour: Int, val count: Int)
data class CountryStats(val country: String, val dxcc: Int?, val worked: Int, val confirmed: Int)
data class StateStats(val state: String, val worked: Int, val confirmed: Int)
data class GridStats(val gridSquare: String, val worked: Int, val confirmed: Int)
