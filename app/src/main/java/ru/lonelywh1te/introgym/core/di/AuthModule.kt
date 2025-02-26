package ru.lonelywh1te.introgym.core.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.auth.data.AuthRepositoryImpl
import ru.lonelywh1te.introgym.auth.data.AuthService
import ru.lonelywh1te.introgym.auth.data.storage.AuthSharedPreferencesImpl
import ru.lonelywh1te.introgym.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.usecase.ConfirmOtpUseCase
import ru.lonelywh1te.introgym.auth.domain.usecase.SendOtpUseCase
import ru.lonelywh1te.introgym.auth.domain.usecase.SignInUseCase
import ru.lonelywh1te.introgym.auth.domain.usecase.SignUpUseCase
import ru.lonelywh1te.introgym.auth.presentation.viewmodel.ConfirmOtpViewModel
import ru.lonelywh1te.introgym.auth.presentation.viewmodel.SignInViewModel
import ru.lonelywh1te.introgym.auth.presentation.viewmodel.SignUpViewModel
import ru.lonelywh1te.introgym.core.network.RetrofitProvider

val authDataModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(authService = get(), authStorage = get())
    }

    single<AuthService> {
        RetrofitProvider.getUnauthorizedRetrofit().create(AuthService::class.java)
    }

    single<AuthStorage> {
        AuthSharedPreferencesImpl(context = get())
    }
}

val authDomainModule = module {
    factory<SendOtpUseCase> {
        SendOtpUseCase(repository = get())
    }

    factory<SignInUseCase> {
        SignInUseCase(repository = get())
    }

    factory<SignUpUseCase> {
        SignUpUseCase(repository = get())
    }

    factory<ConfirmOtpUseCase> {
        ConfirmOtpUseCase(repository = get())
    }
}

val authPresentationModule = module {
    viewModel<SignInViewModel> {
        SignInViewModel(signInUseCase = get())
    }

    viewModel<SignUpViewModel> {
        SignUpViewModel(sendOtpUseCase = get())
    }

    viewModel<ConfirmOtpViewModel> {
        ConfirmOtpViewModel(confirmOtpUseCase = get())
    }
}

val authModule = module {
    includes(authDataModule, authDomainModule, authPresentationModule)
}