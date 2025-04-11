package ru.lonelywh1te.introgym.features.workout

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExercisePlanRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.WorkoutRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.domain.WorkoutValidator
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.DeleteWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.MoveWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.UpdateWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlansUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.UpdateWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.CreateWorkoutFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExercisePlanEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel

val workoutDataModule = module {
    single<WorkoutRepository> {
        WorkoutRepositoryImpl(
            workoutDao = get(),
            workoutExerciseDao = get(),
            workoutExercisePlanDao = get(),
            db = get(),
        )
    }

    single<WorkoutExerciseRepository> {
        WorkoutExerciseRepositoryImpl(
            db = get(),
            workoutExerciseDao = get(),
            workoutExercisePlanDao = get(),
        )
    }

    single<WorkoutExercisePlanRepository> {
        WorkoutExercisePlanRepositoryImpl(
            workoutExercisePlanDao = get()
        )
    }
}

val workoutDomainModule = module {
    single<WorkoutValidator> { WorkoutValidator }

    factory<GetWorkoutListUseCase> {
        GetWorkoutListUseCase(repository = get())
    }

    factory<CreateWorkoutUseCase> {
        CreateWorkoutUseCase(
            workoutRepository = get(),
            validator = get(),
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
        )
    }

    factory<UpdateWorkoutUseCase> {
        UpdateWorkoutUseCase(
            repository = get(),
        )
    }

    factory<MoveWorkoutUseCase> {
        MoveWorkoutUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutExercisePlansUseCase> {
        GetWorkoutExercisePlansUseCase(
            repository = get(),
        )
    }

    factory<AddWorkoutExerciseUseCase> {
        AddWorkoutExerciseUseCase(
            repository = get(),
        )
    }

    factory<UpdateWorkoutExercisePlanUseCase> {
        UpdateWorkoutExercisePlanUseCase(
            repository = get(),
        )
    }

    factory<UpdateWorkoutExerciseUseCase> {
        UpdateWorkoutExerciseUseCase(
            repository = get(),
        )
    }

    factory<MoveWorkoutExerciseUseCase> {
        MoveWorkoutExerciseUseCase(
            repository = get(),
        )
    }

    factory<DeleteWorkoutExerciseUseCase> {
        DeleteWorkoutExerciseUseCase(
            repository = get(),
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

    viewModel<CreateWorkoutFragmentViewModel> {
        CreateWorkoutFragmentViewModel(
            createWorkoutUseCase = get(),
            getExerciseUseCase = get(),
        )
    }

    viewModel<WorkoutExercisePlanEditorFragmentViewModel> {
        WorkoutExercisePlanEditorFragmentViewModel(
            getExerciseUseCase = get(),
            getWorkoutExercisePlanUseCase = get(),
        )
    }

    viewModel<WorkoutFragmentViewModel> {
        WorkoutFragmentViewModel(
            getWorkoutUseCase = get(),
            getWorkoutExercisesUseCase = get(),
            getWorkoutExerciseItemsByWorkoutIdUseCase = get(),
            addWorkoutExerciseUseCase = get(),
            deleteWorkoutUseCase = get(),
            updateWorkoutExerciseUseCase = get(),
            updateWorkoutExercisePlanUseCase = get(),
            moveWorkoutExerciseUseCase = get(),
            deleteWorkoutExerciseUseCase = get(),
            updateWorkoutUseCase = get(),
        )
    }
}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}