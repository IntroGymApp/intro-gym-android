package ru.lonelywh1te.introgym.features.onboarding.domain.usecase

import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository

class SetOnboardingStateUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(isCompleted: Boolean) {
        repository.setOnboardingCompleted(isCompleted)
    }
}