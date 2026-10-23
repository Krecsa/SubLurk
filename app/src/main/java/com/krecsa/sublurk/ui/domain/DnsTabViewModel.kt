package com.krecsa.sublurk.ui.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.SettingsManager
import com.krecsa.sublurk.data.network.GoogleDnsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DnsTabViewModel @Inject constructor(
    private val googleDnsApi: GoogleDnsApi,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _records = MutableLiveData<List<DnsRecord>>(emptyList())
    val records: LiveData<List<DnsRecord>> = _records

    fun load(domain: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _records.value = emptyList()

            try {
                val hidePrivateIp = settingsManager.hidePrivateIpFlow.first()
                val types = listOf("A", "AAAA", "MX", "TXT", "NS", "CNAME")

                val responses = types.map { type ->
                    async {
                        try {
                            val response = googleDnsApi.resolve(domain, type)
                            if (response.isSuccessful) {
                                response.body()?.answer?.mapNotNull { answer ->
                                    val data = answer.data
                                    val answerType = answer.type
                                    if (data != null && answerType != null) {
                                        val typeStr = when (answerType) {
                                            1 -> "A"
                                            28 -> "AAAA"
                                            15 -> "MX"
                                            16 -> "TXT"
                                            2 -> "NS"
                                            5 -> "CNAME"
                                            else -> "T$answerType"
                                        }
                                        DnsRecord(typeStr, data)
                                    } else null
                                } ?: emptyList()
                            } else {
                                emptyList()
                            }
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }.awaitAll()

                var allRecords = responses.flatten().distinct().sortedBy { it.type }

                if (hidePrivateIp) {
                    allRecords = allRecords.filter { record ->
                        !(record.type == "A" && isPrivateIp(record.data))
                    }
                }

                if (allRecords.isEmpty()) {
                    _error.value = "error_no_dns"
                } else {
                    _records.value = allRecords
                }
            } catch (e: Exception) {
                _error.value = "error_network"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun isPrivateIp(ip: String): Boolean {
        val parts = ip.split(".").mapNotNull { it.toIntOrNull() }
        if (parts.size != 4) return false
        return parts[0] == 10 ||
                (parts[0] == 172 && parts[1] in 16..31) ||
                (parts[0] == 192 && parts[1] == 168)
    }
}