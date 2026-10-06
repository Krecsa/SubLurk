package com.krecsa.sublurk.ui.dive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.repository.SubdomainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiveViewModel @Inject constructor(
    private val repository: SubdomainRepository,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _subdomains = MutableLiveData<List<String>>(emptyList())
    val subdomains: LiveData<List<String>> = _subdomains

    fun dive(domain: String, forceRefresh: Boolean = false) {
        if (domain.isBlank()) {
            _error.value = "Введите домен"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _subdomains.value = emptyList()

            val result = repository.getSubdomains(domain, forceRefresh)

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
}