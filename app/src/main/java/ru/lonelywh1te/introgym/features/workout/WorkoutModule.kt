package ru.lonelywh1te.introgym.features.workout

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExercisePlanRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.ReorderWorkoutsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisePlansUseCase
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExercisePlanEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel

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

    factory<GetWorkoutByIdUseCase> {
        GetWorkoutByIdUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutExerciseItemsByWorkoutIdUseCase> {
        GetWorkoutExerciseItemsByWorkoutIdUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutExercisesUseCase> {
        GetWorkoutExercisesUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutExercisePlanUseCase> {
        GetWorkoutExercisePlanUseCase(
            repository = get(),
        )
    }

    factory<DeleteWorkoutUseCase> {
        DeleteWorkoutUseCase(
            repository = get(),
            reorderWorkoutsUseCase = get(),
        )
    }

    factory<UpdateWorkoutUseCase> {
        UpdateWorkoutUseCase(
            workoutRepository = get(),
            workoutExercisePlanRepository = get(),
            workoutExerciseRepository = get(),
        )
    }

    factory<MoveWorkoutUseCase> {
        MoveWorkoutUseCase(
            repository = get(),
            reorderWorkoutsUseCase = get()
        )
    }

    factory<ReorderWorkoutsUseCase> {
        ReorderWorkoutsUseCase(
            repository = get()
        )
    }

    factory<GetWorkoutExercisePlansUseCase> {
        GetWorkoutExercisePlansUseCase(
            repository = get()
        )
    }
}

val workoutPresentationModule = module {
    viewModel<WorkoutsFragmentViewModel> {
        WorkoutsFragmentViewModel(
            getWorkoutListUseCase = get(),
            moveWorkoutUseCase = get(),
            deleteWorkoutUseCase = get(),
        )
    }

    viewModel<WorkoutEditorFragmentViewModel> {
        WorkoutEditorFragmentViewModel(
            createWorkoutUseCase = get(),
            updateWorkoutUseCase = get(),
            getExerciseUseCase = get(),
            getWorkoutExercisesUseCase = get(),
            getWorkoutByIdUseCase = get(),
            getWorkoutExerciseItemsByWorkoutIdUseCase = get(),
            getWorkoutExercisePlansUseCase = get(),
        )
    }

    viewModel<WorkoutExercisePlanEditorFragmentViewModel> {
        WorkoutExercisePlanEditorFragmentViewModel(
            getExerciseUseCase = get()
        )
    }

    viewModel<WorkoutFragmentViewModel> {
        WorkoutFragmentViewModel(
            getWorkoutUseCase = get(),
            getWorkoutExerciseItemsByWorkoutIdUseCase = get(),
            deleteWorkoutUseCase = get(),
        )
    }
}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}