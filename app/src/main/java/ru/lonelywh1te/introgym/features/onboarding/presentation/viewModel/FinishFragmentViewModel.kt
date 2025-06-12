package ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.CompleteOnboardingUseCase

class FinishFragmentViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
): ViewModel() {

    fun completeOnboarding() {
        completeOnboardingUseCase()
    }

}