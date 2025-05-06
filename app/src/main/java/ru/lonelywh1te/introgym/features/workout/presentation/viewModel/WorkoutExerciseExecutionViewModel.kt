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
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase

class WorkoutExerciseExecutionViewModel(
    private val getWorkoutExerciseUseCase: GetWorkoutExerciseUseCase,
    private val getWorkoutExercisePlanUseCase: GetWorkoutExercisePlanUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val workoutExerciseId: MutableStateFlow<Long> = MutableStateFlow(-1)

    val workoutExercise: StateFlow<WorkoutExercise?> = workoutExerciseId
        .flatMapLatest { id ->
            getWorkoutExerciseUseCase(id).map { result ->
                var workoutExercise: WorkoutExercise? = null

                result
                    .onSuccess { workoutExercise = it }
                    .onFailure { _errors.emit(it) }

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
                    .onFailure { _errors.emit(it) }

                workoutExercisePlan
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

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

    private var effort: Effort? = null

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    fun setWorkoutExerciseId(id: Long) {
        workoutExerciseId.value = id
    }

    fun setEffort(effort: Effort) {
        this.effort = effort
    }
}