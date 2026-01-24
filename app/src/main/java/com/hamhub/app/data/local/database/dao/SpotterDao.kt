package com.hamhub.app.data.local.database.dao

import androidx.room.*
import com.hamhub.app.data.local.entity.SpotterListEntity
import com.hamhub.app.data.local.entity.SpottedCallsignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpotterDao {

    // List operations
    @Query("SELECT * FROM spotter_lists ORDER BY created_at DESC")
    fun getAllLists(): Flow<List<SpotterListEntity>>

    @Query("SELECT * FROM spotter_lists WHERE id = :id")
    suspend fun getListById(id: Long): SpotterListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: SpotterListEntity): Long

    @Update
    suspend fun updateList(list: SpotterListEntity)

    @Query("DELETE FROM spotter_lists WHERE id = :id")
    suspend fun deleteList(id: Long)

    // Callsign operations
    @Query("SELECT * FROM spotted_callsigns WHERE list_id = :listId ORDER BY added_at DESC")
    fun getCallsignsForList(listId: Long): Flow<List<SpottedCallsignEntity>>

    @Query("SELECT COUNT(*) FROM spotted_callsigns WHERE list_id = :listId")
    suspend fun getCallsignCountForList(listId: Long): Int

    @Query("SELECT COUNT(*) FROM spotted_callsigns WHERE list_id = :listId")
    fun getCallsignCountForListFlow(listId: Long): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM spotted_callsigns WHERE list_id = :listId AND callsign = :callsign COLLATE NOCASE)")
    suspend fun isCallsignInList(listId: Long, callsign: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallsign(callsign: SpottedCallsignEntity): Long

    @Query("DELETE FROM spotted_callsigns WHERE id = :id")
    suspend fun deleteCallsign(id: Long)

    @Query("DELETE FROM spotted_callsigns WHERE list_id = :listId AND callsign = :callsign COLLATE NOCASE")
    suspend fun deleteCallsignFromList(listId: Long, callsign: String)

    // Get list with count for display
    @Query("""
        SELECT l.*, COUNT(c.id) as callsignCount
        FROM spotter_lists l
        LEFT JOIN spotted_callsigns c ON l.id = c.list_id
        GROUP BY l.id
        ORDER BY l.created_at DESC
    """)
    fun getListsWithCount(): Flow<List<SpotterListWithCount>>
}

data class SpotterListWithCount(
    val id: Long,
    val name: String,
    val description: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    val callsignCount: Int
)
