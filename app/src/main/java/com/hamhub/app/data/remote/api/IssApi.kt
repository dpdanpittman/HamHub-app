package com.hamhub.app.data.remote.api

import com.hamhub.app.data.remote.dto.N2yoPassesResponse
import com.hamhub.app.data.remote.dto.N2yoPositionResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * N2YO API for satellite tracking.
 * Base URL: https://api.n2yo.com/rest/v1/satellite/
 * ISS NORAD ID: 25544
 *
 * Free tier: 1000 transactions/hour
 * Get API key at: https://www.n2yo.com/api/
 */
interface IssApi {

    companion object {
        const val ISS_NORAD_ID = 25544
        const val DEFAULT_OBSERVER_ALT = 0  // meters above sea level
        const val DEFAULT_SECONDS = 1       // number of positions to return
    }

    /**
     * Get satellite positions.
     *
     * @param id NORAD ID of the satellite (25544 for ISS)
     * @param observerLat Observer's latitude
     * @param observerLng Observer's longitude
     * @param observerAlt Observer's altitude in meters
     * @param seconds Number of future positions to return (each position is 1 second apart)
     * @param apiKey Your N2YO API key
     */
    @GET("positions/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{seconds}")
    suspend fun getPositions(
        @Path("id") id: Int = ISS_NORAD_ID,
        @Path("observer_lat") observerLat: Double,
        @Path("observer_lng") observerLng: Double,
        @Path("observer_alt") observerAlt: Int = DEFAULT_OBSERVER_ALT,
        @Path("seconds") seconds: Int = DEFAULT_SECONDS,
        @Query("apiKey") apiKey: String
    ): N2yoPositionResponse

    /**
     * Get visual passes for a satellite.
     *
     * @param id NORAD ID of the satellite
     * @param observerLat Observer's latitude
     * @param observerLng Observer's longitude
     * @param observerAlt Observer's altitude in meters
     * @param days Number of days to predict passes for (max 10)
     * @param minVisibility Minimum visibility in seconds
     * @param apiKey Your N2YO API key
     */
    @GET("visualpasses/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{days}/{min_visibility}")
    suspend fun getVisualPasses(
        @Path("id") id: Int = ISS_NORAD_ID,
        @Path("observer_lat") observerLat: Double,
        @Path("observer_lng") observerLng: Double,
        @Path("observer_alt") observerAlt: Int = DEFAULT_OBSERVER_ALT,
        @Path("days") days: Int = 7,
        @Path("min_visibility") minVisibility: Int = 60,
        @Query("apiKey") apiKey: String
    ): N2yoPassesResponse

    /**
     * Get radio passes for a satellite (passes where satellite is above horizon).
     *
     * @param id NORAD ID of the satellite
     * @param observerLat Observer's latitude
     * @param observerLng Observer's longitude
     * @param observerAlt Observer's altitude in meters
     * @param days Number of days to predict passes for (max 10)
     * @param minElevation Minimum elevation in degrees
     * @param apiKey Your N2YO API key
     */
    @GET("radiopasses/{id}/{observer_lat}/{observer_lng}/{observer_alt}/{days}/{min_elevation}")
    suspend fun getRadioPasses(
        @Path("id") id: Int = ISS_NORAD_ID,
        @Path("observer_lat") observerLat: Double,
        @Path("observer_lng") observerLng: Double,
        @Path("observer_alt") observerAlt: Int = DEFAULT_OBSERVER_ALT,
        @Path("days") days: Int = 7,
        @Path("min_elevation") minElevation: Int = 10,
        @Query("apiKey") apiKey: String
    ): N2yoPassesResponse
}
