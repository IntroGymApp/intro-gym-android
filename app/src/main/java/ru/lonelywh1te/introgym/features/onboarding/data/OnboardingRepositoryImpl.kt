package ru.lonelywh1te.introgym.features.onboarding.data

import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val userPreferences: UserPreferences,
    private val settingsPreferences: SettingsPreferences,
): OnboardingRepository {
    override fun setUserPreferences(name: String?) {
        TODO("Not yet implemented")
    }

    override fun setNotificationEnabled(isEnabled: Boolean) {
        settingsPreferences.isNotificationEnabled = isEnabled
    }

    override fun setOnboardingCompleted(isCompleted: Boolean) {
        settingsPreferences.onboardingCompleted = isCompleted
    }
}