package com.hamhub.app.data.remote.api

import com.hamhub.app.data.remote.dto.RepeaterBookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RepeaterBookApi {

    @GET("api/export.php")
    suspend fun searchByState(
        @Query("state") state: String,
        @Query("city") city: String? = null
    ): RepeaterBookResponse

    @GET("api/export.php")
    suspend fun searchByLocation(
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("distance") distance: Int = 50
    ): RepeaterBookResponse

    @GET("api/export.php")
    suspend fun searchByCallsign(
        @Query("callsign") callsign: String
    ): RepeaterBookResponse

    @GET("api/export.php")
    suspend fun searchByFrequency(
        @Query("frequency") frequency: String,
        @Query("state") state: String? = null
    ): RepeaterBookResponse
}
