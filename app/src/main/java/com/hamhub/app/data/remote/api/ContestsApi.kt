package com.hamhub.app.data.remote.api

import retrofit2.http.GET

/**
 * API for fetching contest calendar data from WA7BNM.
 * https://www.contestcalendar.com/
 */
interface ContestsApi {

    /**
     * Get the 5-week contest calendar in iCal format.
     */
    @GET("fivewkcal.ics")
    suspend fun getContestCalendar(): String
}
