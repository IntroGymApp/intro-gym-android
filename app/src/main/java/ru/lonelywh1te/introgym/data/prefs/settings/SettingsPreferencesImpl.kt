package ru.lonelywh1te.introgym.data.prefs.settings

import android.content.Context
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences

class SettingsPreferencesImpl(context: Context): SettingsPreferences {
    private val prefs = context.getSharedPreferences(SETTINGS_PREFS_KEY, Context.MODE_PRIVATE)

    override fun setIsDarkTheme(state: Boolean) {
        prefs.edit().putBoolean(DARK_THEME_KEY, state).apply()
    }

    override fun getIsDarkTheme(): Boolean {
        return prefs.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setIsNotificationEnabled(state: Boolean) {
        prefs.edit().putBoolean(NOTIFICATION_ENABLED_KEY, state).apply()
    }

    override fun getIsNotificationEnabled(): Boolean {
        return prefs.getBoolean(NOTIFICATION_ENABLED_KEY, false)
    }

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val SETTINGS_PREFS_KEY = "settings_preferences"

        private const val DARK_THEME_KEY = "dark_theme"
        private const val NOTIFICATION_ENABLED_KEY = "notification_enabled"
    }
}