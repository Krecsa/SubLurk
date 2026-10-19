package com.krecsa.sublurk.data.network

import com.google.gson.annotations.SerializedName

data class WhoisRequest(
    @SerializedName("jsonrpc") val jsonrpc: String = "2.0",
    @SerializedName("id") val id: Int = 1,
    @SerializedName("method") val method: String = "tools/call",
    @SerializedName("params") val params: WhoisParams
)

data class WhoisParams(
    @SerializedName("name") val name: String = "whois_lookup",
    @SerializedName("arguments") val arguments: WhoisArguments
)

data class WhoisArguments(
    @SerializedName("domain") val domain: String
)

data class WhoisResponse(
    @SerializedName("result") val result: WhoisResult?
)

data class WhoisResult(
    @SerializedName("content") val content: List<WhoisContent>?
)

data class WhoisContent(
    @SerializedName("type") val type: String?,
    @SerializedName("text") val text: String?
)