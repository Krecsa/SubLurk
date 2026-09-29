package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class CrtShEntry(
    @SerializedName("issuer_ca_id") val issuerCaId: Long?,
    @SerializedName("issuer_name") val issuerName: String?,
    @SerializedName("common_name") val commonName: String?,
    @SerializedName("name_value") val nameValue: String?,
    @SerializedName("id") val id: Long?,
    @SerializedName("entry_timestamp") val entryTimestamp: String?,
    @SerializedName("not_before") val notBefore: String?,
    @SerializedName("not_after") val notAfter: String?,
)