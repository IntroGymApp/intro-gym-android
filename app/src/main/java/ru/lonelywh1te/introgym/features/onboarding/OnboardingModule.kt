package ru.lonelywh1te.introgym.features.onboarding

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.onboarding.data.OnboardingRepositoryImpl
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.CompleteOnboardingUseCase
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetNotificationStateUseCase
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.FinishFragmentViewModel
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.SetNotificationViewModel

val onboardingDataModule = module {

    single<OnboardingRepository> {
        OnboardingRepositoryImpl(
            launchPreferences = get(),
            settingsPreferences = get(),
        )
    }

}

val onboardingDomainModule = module {
    
    factory<SetNotificationStateUseCase> {
        SetNotificationStateUseCase(repository = get())
    }

    factory<CompleteOnboardingUseCase> {
        CompleteOnboardingUseCase(repository = get())
    }

}

val onboardingPresentationModule = module {

    viewModel<SetNotificationViewModel> {
        SetNotificationViewModel(setNotificationStateUseCase = get())
    }

    viewModel<FinishFragmentViewModel> {
        FinishFragmentViewModel(
            completeOnboardingUseCase = get()
        )
    }
}

val onboardingModule = module {
    includes(onboardingDataModule, onboardingDomainModule, onboardingPresentationModule)
}