package ru.lonelywh1te.introgym.data.prefs

interface SettingsPreferences {
    var isFirstLaunch: Boolean
    var onboardingCompleted: Boolean
    var isDarkTheme: Boolean
    var isNotificationEnabled: Boolean

    fun clearAll()
}