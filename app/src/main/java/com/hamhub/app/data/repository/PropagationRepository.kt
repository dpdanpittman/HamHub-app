package com.hamhub.app.data.repository

import com.hamhub.app.data.remote.api.PropagationApi
import com.hamhub.app.data.remote.dto.PropagationData
import com.hamhub.app.data.remote.dto.PropagationParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropagationRepository @Inject constructor(
    private val propagationApi: PropagationApi
) {
    private var cachedData: PropagationData? = null
    private var lastFetchTime: Long = 0

    suspend fun getPropagationData(forceRefresh: Boolean = false): Result<PropagationData> {
        return withContext(Dispatchers.IO) {
            try {
                // Return cached data if valid
                cachedData?.let { cached ->
                    if (!forceRefresh && System.currentTimeMillis() - lastFetchTime < CACHE_VALIDITY_MS) {
                        return@withContext Result.success(cached)
                    }
                }

                val xmlData = propagationApi.getSolarData()
                val data = PropagationParser.parse(xmlData)

                cachedData = data
                lastFetchTime = System.currentTimeMillis()

                Result.success(data)
            } catch (e: Exception) {
                // Return cached data if available, even if stale
                cachedData?.let {
                    Result.success(it)
                } ?: Result.failure(e)
            }
        }
    }

    companion object {
        // Cache propagation data for 15 minutes to reduce API calls
        private const val CACHE_VALIDITY_MS = 15 * 60 * 1000L
    }
}
