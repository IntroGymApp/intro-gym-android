package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseSetUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseSetsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase

class WorkoutExerciseExecutionViewModel(
    private val getWorkoutExerciseUseCase: GetWorkoutExerciseUseCase,
    private val getWorkoutExercisePlanUseCase: GetWorkoutExercisePlanUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,

    private val addWorkoutExerciseSetUseCase: AddWorkoutExerciseSetUseCase,
    private val getWorkoutExerciseSetsUseCase: GetWorkoutExerciseSetsUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val workoutExerciseId: MutableStateFlow<Long> = MutableStateFlow(-1)

    val workoutExercise: StateFlow<WorkoutExercise?> = workoutExerciseId
        .flatMapLatest { id ->
            getWorkoutExerciseUseCase(id).map { result ->
                var workoutExercise: WorkoutExercise? = null

                result
                    .onSuccess { workoutExercise = it }
                    .onFailure { errorDispatcher.dispatch(it) }

                workoutExercise
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val workoutExercisePlan: StateFlow<WorkoutExercisePlan?> = workoutExerciseId
        .flatMapLatest { id ->
            getWorkoutExercisePlanUseCase(id).map { result ->
                var workoutExercisePlan: WorkoutExercisePlan? = null

                result
                    .onSuccess { workoutExercisePlan = it }
                    .onFailure { errorDispatcher.dispatch(it) }

                workoutExercisePlan
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val workoutExerciseSets: StateFlow<List<WorkoutExerciseSet>> = workoutExerciseId
        .flatMapLatest { id ->
            var list: List<WorkoutExerciseSet> = emptyList()

            getWorkoutExerciseSetsUseCase(id).map { result ->
                result
                    .onSuccess { list = it }
                    .onFailure { errorDispatcher.dispatch(it) }


                list
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val exercise: StateFlow<Exercise?> = workoutExercise
        .flatMapLatest { workoutExercise ->
            if (workoutExercise != null) {
                getExerciseUseCase(workoutExercise.exerciseId)
            }
            else {
                flowOf(null)
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private var effort: Effort = Effort.WARMUP

    fun setWorkoutExerciseId(id: Long) {
        workoutExerciseId.value = id
    }

    fun setEffort(effort: Effort) {
        this.effort = effort
    }

    fun addWorkoutExerciseSet(
        reps: String,
        weight: String,
        distance: String,
        timeInSeconds: String,
    ) {
        viewModelScope.launch(dispatcher) {
            val set = WorkoutExerciseSet(
                workoutExerciseId = workoutExerciseId.value,
                reps = if (reps.isBlank()) null else reps.toInt(),
                weightKg = if (weight.isBlank()) null else weight.toFloat(),
                timeInSeconds = if (timeInSeconds.isBlank()) null else timeInSeconds.toInt(),
                distanceInMeters = if (distance.isBlank()) null else distance.toInt(),
                effort = effort,
            )

            addWorkoutExerciseSetUseCase(set)
                .onFailure { errorDispatcher.dispatch(it) }
        }
    }
}