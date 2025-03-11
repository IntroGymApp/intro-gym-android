package ru.lonelywh1te.introgym.features.onboarding

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.onboarding.data.OnboardingRepositoryImpl
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetNotificationStateUseCase
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetUserPreferencesUseCase
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.AboutUserViewModel
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.SetNotificationViewModel

val onboardingDataModule = module {

    single<OnboardingRepository> {
        OnboardingRepositoryImpl(
            userPreferences = get(),
            settingsPreferences = get(),
        )
    }

}

val onboardingDomainModule = module {

    factory<SetUserPreferencesUseCase> {
        SetUserPreferencesUseCase(repository = get())
    }

    factory<SetNotificationStateUseCase> {
        SetNotificationStateUseCase(repository = get())
    }

}

val onboardingPresentationModule = module {

    viewModel<AboutUserViewModel> {
        AboutUserViewModel(setUserPreferencesUseCase = get())
    }

    viewModel<SetNotificationViewModel> {
        SetNotificationViewModel(setNotificationStateUseCase = get())
    }

}

val onboardingModule = module {
    includes(onboardingDataModule, onboardingDomainModule, onboardingPresentationModule)
}