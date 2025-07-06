package ru.lonelywh1te.introgym.features.workout

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.workout.data.repository.WorkoutExercisePlanRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.repository.WorkoutExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.repository.WorkoutExerciseSetRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.repository.WorkoutLogRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.data.repository.WorkoutRepositoryImpl
import ru.lonelywh1te.introgym.features.workout.domain.WorkoutValidator
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutResultsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StartWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StopWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseSetUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.DeleteWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsWithProgressUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseSetsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.MoveWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.UpdateWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlansUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.UpdateWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.GetWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.CreateWorkoutFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExerciseExecutionViewModel
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
            exerciseSetDao = get(),
        )
    }

    single<WorkoutExercisePlanRepository> {
        WorkoutExercisePlanRepositoryImpl(
            workoutExerciseDao = get(),
            workoutExercisePlanDao = get(),
        )
    }

    single<WorkoutExerciseSetRepository> {
        WorkoutExerciseSetRepositoryImpl(
            exerciseSetDao = get(),
            workoutExerciseDao = get(),
        )
    }

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

    factory<GetWorkoutExerciseItemsUseCase> {
        GetWorkoutExerciseItemsUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutExerciseItemsWithProgressUseCase> {
        GetWorkoutExerciseItemsWithProgressUseCase(
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

    factory<StartWorkoutUseCase> {
        StartWorkoutUseCase(
            repository = get()
        )
    }

    factory<StopWorkoutUseCase> {
        StopWorkoutUseCase(
            repository = get()
        )
    }

    factory<GetWorkoutExerciseUseCase> {
        GetWorkoutExerciseUseCase(
            repository = get()
        )
    }

    factory<GetWorkoutLogUseCase> {
        GetWorkoutLogUseCase(
            repository = get()
        )
    }

    factory<GetWorkoutExerciseSetsUseCase> {
        GetWorkoutExerciseSetsUseCase(
            repository = get()
        )
    }

    factory<AddWorkoutExerciseSetUseCase> {
        AddWorkoutExerciseSetUseCase(
            repository = get()
        )
    }

    factory<GetWorkoutResultsUseCase> {
        GetWorkoutResultsUseCase(
            workoutLogRepository = get(),
            workoutExerciseSetRepository = get(),
            workoutExercisePlanRepository = get(),
        )
    }
}

val workoutPresentationModule = module {
    viewModel<WorkoutsFragmentViewModel> {
        WorkoutsFragmentViewModel(
            getWorkoutListUseCase = get(),
            moveWorkoutUseCase = get(),
            deleteWorkoutUseCase = get(),
            errorDispatcher = get(),
        )
    }

    viewModel<CreateWorkoutFragmentViewModel> {
        CreateWorkoutFragmentViewModel(
            createWorkoutUseCase = get(),
            getExerciseUseCase = get(),
            errorDispatcher = get(),
        )
    }

    viewModel<WorkoutExercisePlanEditorFragmentViewModel> {
        WorkoutExercisePlanEditorFragmentViewModel(
            getExerciseUseCase = get(),
            getWorkoutExercisePlanUseCase = get(),
            getWorkoutExerciseUseCase = get(),
            updateWorkoutExerciseUseCase = get(),
            updateWorkoutExercisePlanUseCase = get(),
            errorDispatcher = get(),
        )
    }

    viewModel<WorkoutFragmentViewModel> {
        WorkoutFragmentViewModel(
            getWorkoutUseCase = get(),
            getWorkoutExerciseItemsUseCase = get(),
            addWorkoutExerciseUseCase = get(),
            deleteWorkoutUseCase = get(),
            moveWorkoutExerciseUseCase = get(),
            deleteWorkoutExerciseUseCase = get(),
            updateWorkoutUseCase = get(),
            getWorkoutExerciseItemsWithProgressUseCase = get(),
            startWorkoutUseCase = get(),
            stopWorkoutUseCase = get(),
            getWorkoutLogUseCase = get(),
            getWorkoutResultsUseCase = get(),
            errorDispatcher = get(),
        )
    }

    viewModel<WorkoutExerciseExecutionViewModel> {
        WorkoutExerciseExecutionViewModel(
            getExerciseUseCase = get(),
            getWorkoutExerciseUseCase = get(),
            getWorkoutExercisePlanUseCase = get(),
            getWorkoutExerciseSetsUseCase = get(),
            addWorkoutExerciseSetUseCase = get(),
            errorDispatcher = get(),
        )
    }
}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}