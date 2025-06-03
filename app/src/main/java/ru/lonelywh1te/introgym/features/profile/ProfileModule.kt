package ru.lonelywh1te.introgym.features.profile

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.profile.data.UserInfoRepositoryImpl
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository
import ru.lonelywh1te.introgym.features.profile.domain.usecase.GetUserInfoUseCase
import ru.lonelywh1te.introgym.features.profile.presentation.ProfileFragmentViewModel

private val profileDataModule = module {
    single<UserInfoRepository> {
        UserInfoRepositoryImpl(
            userPreferences = get(),
            workoutDao = get(),
        )
    }
}

private val profileDomainModule = module {
    factory<GetUserInfoUseCase> {
        GetUserInfoUseCase(
            repository = get(),
        )
    }
}

private val profilePresentationModule = module {

    viewModel<ProfileFragmentViewModel> {
        ProfileFragmentViewModel(
            getUserInfoUseCase = get(),
            authRepository = get(),
        )
    }

}

val profileModule = module {
    includes(profileDataModule, profileDomainModule, profilePresentationModule)
}