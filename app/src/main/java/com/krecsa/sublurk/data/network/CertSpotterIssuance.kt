package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class CertSpotterIssuance(
    @SerializedName("dns_names") val dnsNames: List<String>?
)