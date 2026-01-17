package com.hamhub.app.data.local.database.dao

import androidx.room.*
import com.hamhub.app.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingsEntity>>

    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Query("SELECT value FROM settings WHERE key = :key")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT value FROM settings WHERE key = :key")
    fun getSettingValueFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)

    @Query("INSERT OR REPLACE INTO settings (key, value) VALUES (:key, :value)")
    suspend fun setSetting(key: String, value: String)

    @Delete
    suspend fun deleteSetting(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSettingByKey(key: String)

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()

    companion object {
        // Setting keys
        const val KEY_MY_CALLSIGN = "my_callsign"
        const val KEY_MY_GRID = "my_grid"
        const val KEY_THEME = "theme"
        const val KEY_DEFAULT_POWER = "default_power"
        const val KEY_DEFAULT_MODE = "default_mode"
        const val KEY_DEFAULT_BAND = "default_band"
        const val KEY_N2YO_API_KEY = "n2yo_api_key"
    }
}
