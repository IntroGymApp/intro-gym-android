package ru.lonelywh1te.introgym.features.profile

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.profile.presentation.ProfileFragmentViewModel

private val profileDataModule = module {

}

private val profileDomainModule = module {

}

private val profilePresentationModule = module {

    viewModel<ProfileFragmentViewModel> {
        ProfileFragmentViewModel(
            authRepository = get(),
            userPreferences = get(),
        )
    }

}

val profileModule = module {
    includes(profileDataModule, profileDomainModule, profilePresentationModule)
}