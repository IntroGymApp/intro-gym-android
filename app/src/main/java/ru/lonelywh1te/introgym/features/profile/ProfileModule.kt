package ru.lonelywh1te.introgym.features.profile

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.network.RetrofitProvider
import ru.lonelywh1te.introgym.features.profile.data.ProfileApi
import ru.lonelywh1te.introgym.features.profile.data.UserInfoRepositoryImpl
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository
import ru.lonelywh1te.introgym.features.profile.domain.usecase.GetUserInfoUseCase
import ru.lonelywh1te.introgym.features.profile.domain.usecase.SignOutUseCase
import ru.lonelywh1te.introgym.features.profile.presentation.ProfileFragmentViewModel

private val profileDataModule = module {
    single<UserInfoRepository> {
        UserInfoRepositoryImpl(
            userPreferences = get(),
            profileApi = get(),
        )
    }

    single<ProfileApi> {
        RetrofitProvider.getAuthorizedRetrofit(
            authenticator = get(),
            authInterceptor = get(),
        ).create(ProfileApi::class.java)
    }
}

private val profileDomainModule = module {

    factory<GetUserInfoUseCase> {
        GetUserInfoUseCase(
            repository = get(),
        )
    }

    factory<SignOutUseCase> {
        SignOutUseCase(
            authStorage = get(),
            userPreferences = get(),
        )
    }
}

private val profilePresentationModule = module {

    viewModel<ProfileFragmentViewModel> {
        ProfileFragmentViewModel(
            getUserInfoUseCase = get(),
            signOutUseCase = get(),
            authRepository = get(),
        )
    }

}

val profileModule = module {
    includes(profileDataModule, profileDomainModule, profilePresentationModule)
}