package com.krecsa.sublurk.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "system"
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "system"
    }

    fun applyThemeFromPrefs() {
        val theme = runBlocking {
            themeFlow.first()
        }
        applyTheme(theme, immediate = true)
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme
        }
        applyTheme(theme, immediate = false)
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
        applyLanguage(language)
    }

    fun applyTheme(theme: String, immediate: Boolean) {
        val applyAction = {
            when (theme) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        if (immediate) {
            applyAction()
        } else {
            Handler(Looper.getMainLooper()).postDelayed(applyAction, 100)
        }
    }

    fun applyLanguage(language: String) {
        val locale = when (language) {
            "en" -> LocaleListCompat.forLanguageTags("en")
            "ru" -> LocaleListCompat.forLanguageTags("ru")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(locale)
    }
}