package com.hamhub.app.data.remote.api

import com.hamhub.app.data.remote.dto.IssPosition
import retrofit2.http.GET

interface IssApi {

    @GET("iss-now.json")
    suspend fun getCurrentPosition(): IssPosition
}
