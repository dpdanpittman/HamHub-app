package com.hamhub.app.data.repository

import com.hamhub.app.data.local.SecureStorage
import com.hamhub.app.data.local.database.dao.SettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val secureStorage: SecureStorage
) {
    // StateFlows for API keys (since EncryptedSharedPreferences doesn't support Flow)
    private val _n2yoApiKeyFlow = MutableStateFlow<String?>(null)
    private val _repeaterBookApiKeyFlow = MutableStateFlow<String?>(null)

    init {
        // Initialize flows with current values
        _n2yoApiKeyFlow.value = secureStorage.getString(SecureStorage.KEY_N2YO_API_KEY)
        _repeaterBookApiKeyFlow.value = secureStorage.getString(SecureStorage.KEY_REPEATERBOOK_API_KEY)
    }

    suspend fun getMyCallsign(): String? = settingsDao.getSettingValue(SettingsDao.KEY_MY_CALLSIGN)

    fun getMyCallsignFlow(): Flow<String?> = settingsDao.getSettingValueFlow(SettingsDao.KEY_MY_CALLSIGN)

    suspend fun setMyCallsign(callsign: String) {
        settingsDao.setSetting(SettingsDao.KEY_MY_CALLSIGN, callsign.uppercase().trim())
    }

    suspend fun getMyGrid(): String? = settingsDao.getSettingValue(SettingsDao.KEY_MY_GRID)

    fun getMyGridFlow(): Flow<String?> = settingsDao.getSettingValueFlow(SettingsDao.KEY_MY_GRID)

    suspend fun setMyGrid(grid: String) {
        settingsDao.setSetting(SettingsDao.KEY_MY_GRID, grid.uppercase().trim())
    }

    suspend fun getTheme(): String? = settingsDao.getSettingValue(SettingsDao.KEY_THEME)

    fun getThemeFlow(): Flow<String?> = settingsDao.getSettingValueFlow(SettingsDao.KEY_THEME)

    suspend fun setTheme(theme: String) {
        settingsDao.setSetting(SettingsDao.KEY_THEME, theme)
    }

    suspend fun getDefaultPower(): Int? {
        return settingsDao.getSettingValue(SettingsDao.KEY_DEFAULT_POWER)?.toIntOrNull()
    }

    fun getDefaultPowerFlow(): Flow<Int?> {
        return settingsDao.getSettingValueFlow(SettingsDao.KEY_DEFAULT_POWER)
            .map { it?.toIntOrNull() }
    }

    suspend fun setDefaultPower(power: Int) {
        settingsDao.setSetting(SettingsDao.KEY_DEFAULT_POWER, power.toString())
    }

    suspend fun getDefaultMode(): String? = settingsDao.getSettingValue(SettingsDao.KEY_DEFAULT_MODE)

    fun getDefaultModeFlow(): Flow<String?> = settingsDao.getSettingValueFlow(SettingsDao.KEY_DEFAULT_MODE)

    suspend fun setDefaultMode(mode: String) {
        settingsDao.setSetting(SettingsDao.KEY_DEFAULT_MODE, mode)
    }

    suspend fun getDefaultBand(): String? = settingsDao.getSettingValue(SettingsDao.KEY_DEFAULT_BAND)

    fun getDefaultBandFlow(): Flow<String?> = settingsDao.getSettingValueFlow(SettingsDao.KEY_DEFAULT_BAND)

    suspend fun setDefaultBand(band: String) {
        settingsDao.setSetting(SettingsDao.KEY_DEFAULT_BAND, band)
    }

    suspend fun clearAllSettings() {
        settingsDao.deleteAllSettings()
    }

    // N2YO API Key (stored securely in EncryptedSharedPreferences)
    fun getN2yoApiKey(): String? = secureStorage.getString(SecureStorage.KEY_N2YO_API_KEY)

    fun getN2yoApiKeyFlow(): Flow<String?> = _n2yoApiKeyFlow

    fun setN2yoApiKey(apiKey: String) {
        val trimmedKey = apiKey.trim()
        secureStorage.putString(SecureStorage.KEY_N2YO_API_KEY, trimmedKey)
        _n2yoApiKeyFlow.value = trimmedKey
    }

    // RepeaterBook API Key (stored securely in EncryptedSharedPreferences)
    fun getRepeaterBookApiKey(): String? = secureStorage.getString(SecureStorage.KEY_REPEATERBOOK_API_KEY)

    fun getRepeaterBookApiKeyFlow(): Flow<String?> = _repeaterBookApiKeyFlow

    fun setRepeaterBookApiKey(apiKey: String) {
        val trimmedKey = apiKey.trim()
        secureStorage.putString(SecureStorage.KEY_REPEATERBOOK_API_KEY, trimmedKey)
        _repeaterBookApiKeyFlow.value = trimmedKey
    }

    // Clear all secure storage (for data management)
    fun clearSecureStorage() {
        secureStorage.clear()
        _n2yoApiKeyFlow.value = null
        _repeaterBookApiKeyFlow.value = null
    }
}
