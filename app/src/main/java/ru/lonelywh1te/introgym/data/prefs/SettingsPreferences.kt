package ru.lonelywh1te.introgym.data.prefs

interface SettingsPreferences {
    fun setIsDarkTheme(state: Boolean)
    fun getIsDarkTheme(): Boolean

    fun setIsNotificationEnabled(state: Boolean)
    fun getIsNotificationEnabled(): Boolean

    fun clearAll()
}