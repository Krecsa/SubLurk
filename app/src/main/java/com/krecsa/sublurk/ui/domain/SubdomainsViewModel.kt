package com.krecsa.sublurk.ui.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.repository.SubdomainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubdomainsViewModel @Inject constructor(
    private val repository: SubdomainRepository,
) : ViewModel() {

    private val _subdomains = MutableLiveData<List<String>>(emptyList())
    val subdomains: LiveData<List<String>> = _subdomains

    fun load(domain: String) {
        viewModelScope.launch {
            val result = repository.getSubdomains(domain)
            result.onSuccess { _subdomains.value = it }
        }
    }
}