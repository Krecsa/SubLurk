package com.krecsa.sublurk.ui.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.network.GoogleDnsApi
import com.krecsa.sublurk.data.network.IpInfoApi
import com.krecsa.sublurk.data.network.IpInfoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IpTabViewModel @Inject constructor(
    private val googleDnsApi: GoogleDnsApi,
    private val ipInfoApi: IpInfoApi,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _ip = MutableLiveData<String?>(null)
    val ip: LiveData<String?> = _ip

    private val _info = MutableLiveData<IpInfoResponse?>(null)
    val info: LiveData<IpInfoResponse?> = _info

    fun load(domain: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                var ipAddress = resolveIp(domain, "A")

                if (ipAddress.isNullOrBlank() || !isValidIp(ipAddress)) {
                    val cname = resolveIp(domain, "CNAME")
                    if (!cname.isNullOrBlank()) {
                        ipAddress = resolveIp(cname, "A")
                    }
                }

                if (ipAddress.isNullOrBlank() || !isValidIp(ipAddress)) {
                    _error.value = "Не удалось определить IP"
                    _loading.value = false
                    return@launch
                }

                _ip.value = ipAddress

                val ipInfoResponse = ipInfoApi.lookup(ipAddress)
                if (ipInfoResponse.isSuccessful) {
                    _info.value = ipInfoResponse.body()
                } else {
                    _error.value = "Ошибка IPinfo: ${ipInfoResponse.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun resolveIp(domain: String, type: String): String? {
        return try {
            val response = googleDnsApi.resolve(domain, type)
            response.body()?.answer?.firstOrNull()?.data
        } catch (e: Exception) {
            null
        }
    }

    private fun isValidIp(value: String): Boolean {
        return value.matches(Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"""))
    }
}