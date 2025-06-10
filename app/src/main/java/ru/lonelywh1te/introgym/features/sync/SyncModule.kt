package ru.lonelywh1te.introgym.features.sync

import org.koin.core.scope.get
import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.network.RetrofitProvider
import ru.lonelywh1te.introgym.features.sync.data.SyncApi
import ru.lonelywh1te.introgym.features.sync.data.SyncRepositoryImpl
import ru.lonelywh1te.introgym.features.sync.domain.SyncRepository

val syncModule = module {
    single<SyncApi> {
        RetrofitProvider.getAuthorizedRetrofit(
            authenticator = get(),
            authInterceptor = get(),
        ).create(SyncApi::class.java)
    }

    single<SyncRepository> {
        SyncRepositoryImpl(
            syncApi = get(),
            workoutLogDao = get(),
            workoutDao = get(),
            workoutExerciseDao = get(),
            workoutExercisePlanDao = get(),
            exerciseSetDao = get(),
        )
    }
}