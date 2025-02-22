package ru.lonelywh1te.introgym.core.di

import org.koin.dsl.module
import ru.lonelywh1te.introgym.auth.data.AuthRepositoryImpl
import ru.lonelywh1te.introgym.auth.data.AuthService
import ru.lonelywh1te.introgym.auth.data.prefs.AuthSharedPreferencesImpl
import ru.lonelywh1te.introgym.auth.data.prefs.AuthStorage
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.core.network.RetrofitProvider

val authModule = module {
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