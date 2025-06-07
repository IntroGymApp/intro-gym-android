package ru.lonelywh1te.introgym.data.prefs

interface LaunchPreferences {
    fun setIsFirstLaunch(state: Boolean)
    fun getIsFirstLaunch(): Boolean

    fun setIsOnboardingCompleted(state: Boolean)
    fun getIsOnboardingCompleted(): Boolean

    fun clearAll()
}