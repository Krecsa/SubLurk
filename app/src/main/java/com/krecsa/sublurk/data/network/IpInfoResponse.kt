package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class IpInfoResponse(
    @SerializedName("ip") val ip: String?,
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("loc") val loc: String?,
    @SerializedName("org") val org: String?,
    @SerializedName("postal") val postal: String?,
    @SerializedName("timezone") val timezone: String?,
)