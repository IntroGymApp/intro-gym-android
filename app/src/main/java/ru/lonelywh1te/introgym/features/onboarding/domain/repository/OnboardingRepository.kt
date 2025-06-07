package ru.lonelywh1te.introgym.features.onboarding.domain.repository

interface OnboardingRepository {
    fun setNotificationEnabled(isEnabled: Boolean)
    fun setOnboardingCompleted(isCompleted: Boolean)
}