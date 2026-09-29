package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrtShApi {

    @GET("?output=json")
    suspend fun search(
        @Query("q") domain: String,
    ): Response<List<CrtShEntry>>
}