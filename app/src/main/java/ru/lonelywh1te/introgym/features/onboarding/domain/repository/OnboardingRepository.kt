package ru.lonelywh1te.introgym.features.onboarding.domain.repository

import ru.lonelywh1te.introgym.data.prefs.user.Gender
import java.time.LocalDate

interface OnboardingRepository {
    fun setUserPreferences(name: String?, gender: Gender?, birthday: LocalDate?)
    fun setNotificationEnabled(isEnabled: Boolean)
    fun setOnboardingCompleted(isCompleted: Boolean)
}