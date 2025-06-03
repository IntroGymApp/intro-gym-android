package ru.lonelywh1te.introgym.features.onboarding.data

import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.data.prefs.user.Gender
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository
import java.time.LocalDate

class OnboardingRepositoryImpl(
    private val userPreferences: UserPreferences,
    private val settingsPreferences: SettingsPreferences,
): OnboardingRepository {
    override fun setUserPreferences(name: String?, gender: Gender?, birthday: LocalDate?) {
        userPreferences.username = name
        userPreferences.gender = gender
        userPreferences.birthday = birthday
    }

    override fun setNotificationEnabled(isEnabled: Boolean) {
        settingsPreferences.isNotificationEnabled = isEnabled
    }

    override fun setOnboardingCompleted(isCompleted: Boolean) {
        settingsPreferences.onboardingCompleted = isCompleted
    }
}