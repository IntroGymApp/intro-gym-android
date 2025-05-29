package ru.lonelywh1te.introgym.features.home

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.home.data.WorkoutLogRepositoryImpl
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.home.domain.usecase.AddWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogDatesUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogItemListUseCase
import ru.lonelywh1te.introgym.features.home.presentation.viewModel.HomeFragmentViewModel

val homeDataModule = module {
    single<WorkoutLogRepository> {
        WorkoutLogRepositoryImpl(
            db = get(),
            workoutLogDao = get(),
            workoutDao = get(),
            workoutExerciseDao = get(),
            workoutExercisePlanDao = get(),
        )
    }
}

val homeDomainModule = module {
    factory<GetWorkoutLogItemListUseCase> {
        GetWorkoutLogItemListUseCase(
            repository = get(),
        )
    }

    factory<AddWorkoutLogUseCase> {
        AddWorkoutLogUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutLogDatesUseCase> {
        GetWorkoutLogDatesUseCase(
            repository = get()
        )
    }
}

val homePresentationModule = module {
    viewModel<HomeFragmentViewModel> {
        HomeFragmentViewModel(
            addWorkoutLogUseCase = get(),
            getWorkoutLogItemListUseCase = get(),
            getWorkoutLogDatesUseCase = get(),
            deleteWorkoutUseCase = get(),
            errorDispatcher = get(),
        )
    }
}

val homeModule = module {
    includes(homeDataModule, homeDomainModule, homePresentationModule)
}