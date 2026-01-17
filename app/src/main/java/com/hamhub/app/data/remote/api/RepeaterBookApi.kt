package com.hamhub.app.data.remote.api

import com.hamhub.app.data.remote.dto.RepeaterBookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RepeaterBookApi {

    @GET("api/export.php")
    suspend fun searchByState(
        @Query("state_id") stateId: String,
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
        @Query("state_id") stateId: String? = null
    ): RepeaterBookResponse

    companion object {
        // US State FIPS codes for RepeaterBook API
        val STATE_CODES = mapOf(
            "AL" to "01", "AK" to "02", "AZ" to "04", "AR" to "05", "CA" to "06",
            "CO" to "08", "CT" to "09", "DE" to "10", "FL" to "12", "GA" to "13",
            "HI" to "15", "ID" to "16", "IL" to "17", "IN" to "18", "IA" to "19",
            "KS" to "20", "KY" to "21", "LA" to "22", "ME" to "23", "MD" to "24",
            "MA" to "25", "MI" to "26", "MN" to "27", "MS" to "28", "MO" to "29",
            "MT" to "30", "NE" to "31", "NV" to "32", "NH" to "33", "NJ" to "34",
            "NM" to "35", "NY" to "36", "NC" to "37", "ND" to "38", "OH" to "39",
            "OK" to "40", "OR" to "41", "PA" to "42", "RI" to "44", "SC" to "45",
            "SD" to "46", "TN" to "47", "TX" to "48", "UT" to "49", "VT" to "50",
            "VA" to "51", "WA" to "53", "WV" to "54", "WI" to "55", "WY" to "56",
            "DC" to "11", "PR" to "72", "VI" to "78", "GU" to "66", "AS" to "60"
        )

        fun getStateId(stateAbbrev: String): String? = STATE_CODES[stateAbbrev.uppercase()]
    }
}
