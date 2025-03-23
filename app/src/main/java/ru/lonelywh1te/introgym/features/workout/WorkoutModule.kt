package ru.lonelywh1te.introgym.features.workout

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.workout.data.WorkoutRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import ru.lonelywh1te.introgym.features.workout.domain.usecase.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel

val workoutDataModule = module {
    single<WorkoutRepository> {
        WorkoutRepositoryImpl(
            workoutDao = get(),
        )
    }
}

val workoutDomainModule = module {
    factory<GetWorkoutListUseCase> {
        GetWorkoutListUseCase(repository = get())
    }
}

val workoutPresentationModule = module {
    viewModel<WorkoutsFragmentViewModel> {
        WorkoutsFragmentViewModel(
            getWorkoutListUseCase = get(),
        )
    }
}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}