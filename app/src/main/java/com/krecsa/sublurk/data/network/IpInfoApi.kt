package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IpInfoApi {

    @GET("{ip}/json")
    suspend fun lookup(@Path("ip") ip: String): Response<IpInfoResponse>
}