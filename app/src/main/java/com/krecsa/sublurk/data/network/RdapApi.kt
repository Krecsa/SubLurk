package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RdapApi {

    @GET("domain/{domain}")
    suspend fun lookup(@Path("domain") domain: String): Response<RdapResponse>
}