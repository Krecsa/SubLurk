package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface WhoisApi {

    @Headers("Content-Type: application/json")
    @POST("backend/mcp")
    suspend fun lookup(@Body request: WhoisRequest): Response<WhoisResponse>
}