package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class RdapResponse(
    @SerializedName("ldhName") val ldhName: String?,
    @SerializedName("status") val status: List<String>?,
    @SerializedName("events") val events: List<RdapEvent>?,
    @SerializedName("entities") val entities: List<RdapEntity>?,
)

data class RdapEvent(
    @SerializedName("eventAction") val eventAction: String?,
    @SerializedName("eventDate") val eventDate: String?,
)

data class RdapEntity(
    @SerializedName("roles") val roles: List<String>?,
)