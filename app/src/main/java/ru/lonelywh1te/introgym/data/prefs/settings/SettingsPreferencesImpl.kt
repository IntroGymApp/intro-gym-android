package ru.lonelywh1te.introgym.data.prefs.settings

import android.content.Context
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences

class SettingsPreferencesImpl(context: Context): SettingsPreferences {
    private val prefs = context.getSharedPreferences(SETTINGS_PREFS_KEY, Context.MODE_PRIVATE)

    override var isFirstLaunch: Boolean
        get() = prefs.getBoolean(FIRST_LAUNCH_KEY, true)
        set(value) = putBoolean(FIRST_LAUNCH_KEY, value)

    override var isDarkTheme: Boolean
        get() = prefs.getBoolean(DARK_THEME_KEY, false)
        set(value) = putBoolean(DARK_THEME_KEY, value)

    override var isNotificationEnabled: Boolean
        get() = prefs.getBoolean(NOTIFICATION_ENABLED_KEY, false)
        set(value) = putBoolean(NOTIFICATION_ENABLED_KEY, value)

    private fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val SETTINGS_PREFS_KEY = "settings_preferences"
        private const val FIRST_LAUNCH_KEY = "first_launch"
        private const val DARK_THEME_KEY = "dark_theme"
        private const val NOTIFICATION_ENABLED_KEY = "notification_enabled"
    }
}