package com.krecsa.sublurk.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krecsa.sublurk.data.SettingsManager
import com.krecsa.sublurk.data.local.SubdomainDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val subdomainDao: SubdomainDao,
) : ViewModel() {

    val theme = settingsManager.themeFlow
    val language = settingsManager.languageFlow
    val hidePrivateIp = settingsManager.hidePrivateIpFlow

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsManager.setTheme(theme)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(language)
        }
    }

    fun setHidePrivateIp(hide: Boolean) {
        viewModelScope.launch {
            settingsManager.setHidePrivateIp(hide)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            subdomainDao.clearAll()
        }
    }
}