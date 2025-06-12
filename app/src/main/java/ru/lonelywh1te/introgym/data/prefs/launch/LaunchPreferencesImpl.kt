package ru.lonelywh1te.introgym.data.prefs.launch

import android.content.Context
import ru.lonelywh1te.introgym.data.prefs.LaunchPreferences

class LaunchPreferencesImpl(context: Context): LaunchPreferences {
    private val prefs = context.getSharedPreferences(LAUNCH_PREFS_KEY, Context.MODE_PRIVATE)

    override fun setIsFirstLaunch(state: Boolean) {
        prefs.edit().putBoolean(FIRST_LAUNCH_KEY, state).apply()
    }

    override fun getIsFirstLaunch(): Boolean {
        return prefs.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    override fun setIsOnboardingCompleted(state: Boolean) {
        prefs.edit().putBoolean(ONBOARDING_COMPLETED_KEY, state).apply()
    }

    override fun getIsOnboardingCompleted(): Boolean {
        return prefs.getBoolean(ONBOARDING_COMPLETED_KEY, false)
    }

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val LAUNCH_PREFS_KEY = "launch_preferences"

        private const val FIRST_LAUNCH_KEY = "first_launch"
        private const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    }
}