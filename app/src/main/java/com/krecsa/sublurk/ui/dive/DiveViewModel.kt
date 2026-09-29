package com.krecsa.sublurk.ui.dive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.network.CrtShApi
import com.krecsa.sublurk.data.network.CrtShEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiveViewModel @Inject constructor(
    private val crtShApi: CrtShApi,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _subdomains = MutableLiveData<List<String>>(emptyList())
    val subdomains: LiveData<List<String>> = _subdomains

    fun dive(domain: String) {
        if (domain.isBlank()) {
            _error.value = "Введите домен"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _subdomains.value = emptyList()

            val result = fetchSubdomainsWithRetry(domain)

            result.onSuccess { list ->
                _subdomains.value = list
                if (list.isEmpty()) {
                    _error.value = "Поддомены не найдены"
                }
            }.onFailure { e ->
                _error.value = "Ошибка: ${e.message}"
            }

            _loading.value = false
        }
    }

    private suspend fun fetchSubdomainsWithRetry(
        domain: String,
    ): Result<List<String>> {
        val maxAttempts = 3
        var lastError: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                val response = crtShApi.search("%.$domain")

                if (response.isSuccessful) {
                    val entries = response.body().orEmpty()
                    return Result.success(extractSubdomains(entries, domain))
                }

                if (response.code() in listOf(502, 503, 504)) {
                    lastError = Exception("Сервер перегружен (${response.code()}), попытка ${attempt + 1}/$maxAttempts")
                    delay(1000L * (attempt + 1))
                    return@repeat
                }

                return Result.failure(Exception("Ошибка сервера: ${response.code()}"))
            } catch (e: Exception) {
                lastError = e
                delay(1000L * (attempt + 1))
            }
        }

        return Result.failure(lastError ?: Exception("Не удалось получить данные"))
    }

    private fun extractSubdomains(
        entries: List<CrtShEntry>,
        domain: String,
    ): List<String> {
        return entries
            .flatMap { it.nameValue?.split("\n").orEmpty() }
            .map { it.trim().lowercase() }
            .filter { it.endsWith(domain) }
            .map { it.removePrefix("*.") }
            .distinct()
            .sorted()
    }
}