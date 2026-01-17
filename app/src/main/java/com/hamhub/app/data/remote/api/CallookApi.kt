package com.hamhub.app.data.remote.api

import com.hamhub.app.data.remote.dto.CallookResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CallookApi {

    @GET("{callsign}/json")
    suspend fun lookupCallsign(@Path("callsign") callsign: String): CallookResponse
}
