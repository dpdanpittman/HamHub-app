package com.hamhub.app.data.remote.api

import retrofit2.http.GET

/**
 * API for fetching solar and propagation data from HamQSL.
 */
interface PropagationApi {

    @GET("solarxml.php")
    suspend fun getSolarData(): String
}
