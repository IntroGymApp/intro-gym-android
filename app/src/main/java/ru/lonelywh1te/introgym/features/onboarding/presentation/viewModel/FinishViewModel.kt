package ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetOnboardingStateUseCase

class FinishViewModel(
    private val setOnboardingStateUseCase: SetOnboardingStateUseCase,
): ViewModel() {

    fun completeOnboarding() {
        setOnboardingStateUseCase(isCompleted = true)
    }

}