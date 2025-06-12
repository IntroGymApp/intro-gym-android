package ru.lonelywh1te.introgym.features.onboarding.domain.usecase

import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(private val repository: OnboardingRepository) {
    operator fun invoke() {
        repository.setOnboardingCompleted(true)
    }
}