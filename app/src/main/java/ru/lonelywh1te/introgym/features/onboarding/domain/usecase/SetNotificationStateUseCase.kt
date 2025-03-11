package ru.lonelywh1te.introgym.features.onboarding.domain.usecase

import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository

class SetNotificationStateUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(isEnabled: Boolean) {
        repository.setNotificationEnabled(isEnabled)
    }
}