package com.krecsa.sublurk.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CertSpotterApi {

    @GET("v1/issuances")
    suspend fun getIssuances(
        @Query("domain") domain: String,
        @Query("include_subdomains") includeSubdomains: Boolean = true,
        @Query("expand") expand: String = "dns_names"
    ): Response<List<CertSpotterIssuance>>
}