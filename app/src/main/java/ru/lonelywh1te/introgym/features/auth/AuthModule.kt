package ru.lonelywh1te.introgym.features.auth

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.network.RetrofitProvider
import ru.lonelywh1te.introgym.features.auth.data.AuthRepositoryImpl
import ru.lonelywh1te.introgym.features.auth.data.AuthService
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthSharedPreferencesImpl
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ConfirmOtpUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SendOtpUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignInUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignUpUseCase
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.ConfirmOtpViewModel
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignInViewModel
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

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

    single<EmailPasswordValidator> {
        EmailPasswordValidator
    }
}

val authPresentationModule = module {
    viewModel<SignInViewModel> {
        SignInViewModel(signInUseCase = get())
    }

    viewModel<SignUpViewModel> {
        SignUpViewModel(
            sendOtpUseCase = get(),
            signUpUseCase = get(),
            validator = get(),
        )
    }

    viewModel<ConfirmOtpViewModel> {
        ConfirmOtpViewModel(confirmOtpUseCase = get())
    }

}

val authModule = module {
    includes(authDataModule, authDomainModule, authPresentationModule)
}