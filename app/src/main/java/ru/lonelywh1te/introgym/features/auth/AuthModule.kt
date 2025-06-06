package ru.lonelywh1te.introgym.features.auth

import okhttp3.Authenticator
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.network.RetrofitProvider
import ru.lonelywh1te.introgym.features.auth.data.AuthRepositoryImpl
import ru.lonelywh1te.introgym.features.auth.data.AuthApi
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthSharedPreferencesImpl
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.data.token.AuthInterceptor
import ru.lonelywh1te.introgym.features.auth.data.token.TokenAuthenticator
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.OtpValidator
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ChangePasswordUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ConfirmOtpUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SendOtpUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignInUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignUpUseCase
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.ConfirmOtpViewModel
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.RestorePasswordViewModel
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignInViewModel
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

val authDataModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            authStorage = get(),
        )
    }

    single<AuthApi> {
        RetrofitProvider.getUnauthorizedRetrofit().create(AuthApi::class.java)
    }

    single<AuthStorage> {
        AuthSharedPreferencesImpl(context = get())
    }

    single<Authenticator> {
        TokenAuthenticator(
            authStorage = get(),
            authRepository = get(),
        )
    }

    single<AuthInterceptor> {
        AuthInterceptor(
            authStorage = get(),
        )
    }
}

val authDomainModule = module {
    factory<SendOtpUseCase> {
        SendOtpUseCase(
            repository = get(),
            validator = get(),
        )
    }

    factory<SignInUseCase> {
        SignInUseCase(
            repository = get(),
            validator = get(),
        )
    }

    factory<SignUpUseCase> {
        SignUpUseCase(
            repository = get(),
            validator = get(),
        )
    }

    factory<ConfirmOtpUseCase> {
        ConfirmOtpUseCase(
            repository = get(),
            otpValidator = get(),
        )
    }

    factory<ChangePasswordUseCase> {
        ChangePasswordUseCase(
            repository = get(),
            validator = get(),
        )
    }

    single<CredentialsValidator> {
        CredentialsValidator
    }

    single<OtpValidator> {
        OtpValidator
    }
}

val authPresentationModule = module {
    viewModel<SignInViewModel> {
        SignInViewModel(signInUseCase = get())
    }

    viewModel<SignUpViewModel> {
        SignUpViewModel(
            signUpUseCase = get(),
            validator = get(),
        )
    }

    viewModel<ConfirmOtpViewModel> {
        ConfirmOtpViewModel(
            sendOtpUseCase = get(),
            confirmOtpUseCase = get(),
        )
    }

    viewModel<RestorePasswordViewModel> {
        RestorePasswordViewModel(
            changePasswordUseCase = get(),
            validator = get(),
        )
    }

}

val authModule = module {
    includes(authDataModule, authDomainModule, authPresentationModule)
}