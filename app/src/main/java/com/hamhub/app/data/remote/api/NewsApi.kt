package com.hamhub.app.data.remote.api

import retrofit2.http.GET

/**
 * API for fetching ARRL news and DX bulletins RSS feeds.
 */
interface NewsApi {

    /**
     * Get ARRL news RSS feed.
     */
    @GET("arrl.rss")
    suspend fun getArrlNews(): String

    /**
     * Get W1AW DX bulletins RSS feed.
     */
    @GET("w1aw-bulletins.rss")
    suspend fun getDxBulletins(): String
}
