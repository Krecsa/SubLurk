package com.krecsa.sublurk.ui.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.network.GoogleDnsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DnsTabViewModel @Inject constructor(
    private val googleDnsApi: GoogleDnsApi,
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

                val allRecords = responses.flatten()
                    .distinct()
                    .sortedBy { it.type }

                if (allRecords.isEmpty()) {
                    _error.value = "DNS записи не найдены"
                } else {
                    _records.value = allRecords
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}