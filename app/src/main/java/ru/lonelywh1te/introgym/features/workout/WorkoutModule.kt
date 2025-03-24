package ru.lonelywh1te.introgym.features.workout

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExercisePlanRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import ru.lonelywh1te.introgym.features.workout.domain.usecase.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel
import kotlin.math.sin

val workoutDataModule = module {
    single<WorkoutRepository> {
        WorkoutRepositoryImpl(
            workoutDao = get(),
        )
    }

    single<WorkoutExerciseRepository> {
        WorkoutExerciseRepositoryImpl(
            workoutExerciseDao = get()
        )
    }

    single<WorkoutExercisePlanRepository> {
        WorkoutExercisePlanRepositoryImpl(
            workoutExercisePlanDao = get()
        )
    }
}

val workoutDomainModule = module {
    factory<GetWorkoutListUseCase> {
        GetWorkoutListUseCase(repository = get())
    }
    factory<CreateWorkoutUseCase> {
        CreateWorkoutUseCase(
            workoutRepository = get(),
            workoutExercisePlanRepository = get(),
            workoutExerciseRepository = get(),
        )
    }
}

val workoutPresentationModule = module {
    viewModel<WorkoutsFragmentViewModel> {
        WorkoutsFragmentViewModel(
            getWorkoutListUseCase = get(),
        )
    }

    viewModel<WorkoutEditorFragmentViewModel> {
        WorkoutEditorFragmentViewModel(
            createWorkoutUseCase = get(),
            getExerciseUseCase = get(),
        )
    }
}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}