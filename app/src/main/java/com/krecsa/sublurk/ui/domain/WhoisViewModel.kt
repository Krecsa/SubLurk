package com.krecsa.sublurk.ui.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.network.WhoisApi
import com.krecsa.sublurk.data.network.WhoisArguments
import com.krecsa.sublurk.data.network.WhoisParams
import com.krecsa.sublurk.data.network.WhoisRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhoisViewModel @Inject constructor(
    private val whoisApi: WhoisApi,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _whoisData = MutableLiveData<List<Pair<String, String>>>(emptyList())
    val whoisData: LiveData<List<Pair<String, String>>> = _whoisData

    fun load(domain: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _whoisData.value = emptyList()

            try {
                val rootDomain = getRootDomain(domain)
                val request = WhoisRequest(
                    params = WhoisParams(
                        arguments = WhoisArguments(domain = rootDomain)
                    )
                )

                val response = whoisApi.lookup(request)
                if (response.isSuccessful) {
                    val rawText = response.body()?.result?.content?.firstOrNull()?.text
                    if (rawText.isNullOrBlank()) {
                        _error.value = "WHOIS данные пусты"
                    } else {
                        _whoisData.value = parseWhoisText(rawText)
                    }
                } else {
                    _error.value = "Ошибка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка сети: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun getRootDomain(domain: String): String {
        val parts = domain.split(".")
        return if (parts.size >= 2) parts.takeLast(2).joinToString(".") else domain
    }

    private fun parseWhoisText(text: String): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        val lines = text.lines()

        for (line in lines) {
            if (line.isBlank() || line.trim().startsWith("%") || line.trim().startsWith("#")) {
                continue
            }

            val colonIndex = line.indexOf(':')
            if (colonIndex > 0) {
                val key = line.substring(0, colonIndex).trim()
                val value = line.substring(colonIndex + 1).trim()

                val importantKeys = listOf(
                    "domain", "registrar", "org", "created", "paid-till",
                    "free-date", "nserver", "state", "admin-contact", "source"
                )

                if (importantKeys.any { key.equals(it, ignoreCase = true) }) {
                    if (value.isNotBlank()) {
                        result.add(key.replaceFirstChar { it.uppercase() } to value)
                    }
                }
            }
        }

        if (result.isEmpty()) {
            return listOf("WHOIS" to text)
        }

        return result
    }
}