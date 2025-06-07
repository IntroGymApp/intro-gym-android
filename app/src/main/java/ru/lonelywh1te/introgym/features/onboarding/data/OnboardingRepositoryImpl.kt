package ru.lonelywh1te.introgym.features.onboarding.data

import ru.lonelywh1te.introgym.data.prefs.LaunchPreferences
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val launchPreferences: LaunchPreferences,
    private val settingsPreferences: SettingsPreferences,
): OnboardingRepository {
    override fun setNotificationEnabled(isEnabled: Boolean) {
        settingsPreferences.setIsNotificationEnabled(isEnabled)
    }

    override fun setOnboardingCompleted(isCompleted: Boolean) {
        launchPreferences.setIsOnboardingCompleted(isCompleted)
    }
}