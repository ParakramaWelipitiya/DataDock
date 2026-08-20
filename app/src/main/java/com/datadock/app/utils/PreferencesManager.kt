package com.datadock.app.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("datadock_settings", Context.MODE_PRIVATE)

    // Save Results Toggle (Defaults to true)
    fun isDataLoggingEnabled(): Boolean {
        return prefs.getBoolean("data_logging_enabled", true)
    }

    fun setDataLoggingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("data_logging_enabled", enabled).apply()
    }

    // Theme Toggle (Defaults to true for Dark Mode)
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode_enabled", true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("app_language", "en") ?: "en"
    }

    fun setLanguage(langCode: String) {
        prefs.edit().putString("app_language", langCode).apply()
    }
}