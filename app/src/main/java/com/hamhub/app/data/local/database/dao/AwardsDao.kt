package com.hamhub.app.data.local.database.dao

import androidx.room.*
import com.hamhub.app.data.local.entity.AwardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AwardsDao {

    @Query("SELECT * FROM awards WHERE award_type = :awardType")
    fun getAwardsByType(awardType: String): Flow<List<AwardEntity>>

    @Query("""
        SELECT * FROM awards
        WHERE award_type = :awardType
        AND (:band IS NULL OR band = :band)
        AND (:mode IS NULL OR mode = :mode)
    """)
    suspend fun getAwardsFiltered(
        awardType: String,
        band: String? = null,
        mode: String? = null
    ): List<AwardEntity>

    @Query("""
        SELECT COUNT(DISTINCT entity) FROM awards
        WHERE award_type = :awardType
    """)
    suspend fun getWorkedCount(awardType: String): Int

    @Query("""
        SELECT COUNT(DISTINCT entity) FROM awards
        WHERE award_type = :awardType AND confirmed = 1
    """)
    suspend fun getConfirmedCount(awardType: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAward(award: AwardEntity): Long

    @Update
    suspend fun updateAward(award: AwardEntity)

    @Delete
    suspend fun deleteAward(award: AwardEntity)

    @Query("DELETE FROM awards WHERE qso_id = :qsoId")
    suspend fun deleteAwardsByQsoId(qsoId: Long)

    companion object {
        const val TYPE_DXCC = "DXCC"
        const val TYPE_WAS = "WAS"
        const val TYPE_GRID = "GRID"
    }
}
