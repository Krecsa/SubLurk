package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class GoogleDnsResponse(
    @SerializedName("Status") val status: Int?,
    @SerializedName("Answer") val answer: List<GoogleDnsAnswer>?,
)

data class GoogleDnsAnswer(
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: Int?,
    @SerializedName("TTL") val ttl: Int?,
    @SerializedName("data") val data: String?,
)