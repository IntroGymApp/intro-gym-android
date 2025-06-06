package ru.lonelywh1te.introgym.features.onboarding.domain.repository

interface OnboardingRepository {
    fun setUserPreferences(name: String?)
    fun setNotificationEnabled(isEnabled: Boolean)
    fun setOnboardingCompleted(isCompleted: Boolean)
}