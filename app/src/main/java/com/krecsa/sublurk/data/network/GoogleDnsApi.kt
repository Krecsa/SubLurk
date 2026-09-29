package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDnsApi {

    @GET("resolve")
    suspend fun resolve(
        @Query("name") domain: String,
        @Query("type") type: String = "A",
    ): Response<GoogleDnsResponse>
}