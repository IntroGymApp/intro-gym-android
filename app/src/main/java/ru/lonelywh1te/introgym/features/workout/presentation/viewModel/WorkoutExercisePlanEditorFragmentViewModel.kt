package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.UpdateWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.UpdateWorkoutExercisePlanUseCase

class WorkoutExercisePlanEditorFragmentViewModel(
    private val getWorkoutExerciseUseCase: GetWorkoutExerciseUseCase,
    private val getWorkoutExercisePlanUseCase: GetWorkoutExercisePlanUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val updateWorkoutExerciseUseCase: UpdateWorkoutExerciseUseCase,
    private val updateWorkoutExercisePlanUseCase: UpdateWorkoutExercisePlanUseCase,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _workoutExerciseId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val workoutExerciseId: StateFlow<Long?> = _workoutExerciseId

    private val _workoutExercise: MutableStateFlow<WorkoutExercise?> = MutableStateFlow(null)
    val workoutExercise: StateFlow<WorkoutExercise?> = _workoutExercise

    private val _workoutExercisePlan: MutableStateFlow<WorkoutExercisePlan?> = MutableStateFlow(null)
    val workoutExercisePlan: StateFlow<WorkoutExercisePlan?> = _workoutExercisePlan

    val exercise: StateFlow<Exercise?> = _workoutExercise
        .flatMapLatest { workoutExercise ->
            workoutExercise?.let { getExerciseUseCase(it.exerciseId) } ?: flowOf(null)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: SharedFlow<Error> get() = _errors

    fun setWorkoutExerciseId(id: Long) {
        _workoutExerciseId.value = id

        loadWorkoutExercise(id)
        loadWorkoutExercisePlan(id)
    }

    fun setWorkoutExercise(workoutExercise: WorkoutExercise) {
        _workoutExercise.value = workoutExercise
    }

    fun setWorkoutExercisePlan(plan: WorkoutExercisePlan) {
        _workoutExercisePlan.value = plan
    }

    fun setWorkoutPlanExercise(
        sets: String,
        reps: String,
        weight: String,
        timeInSec: String,
        distanceInMeters: String
    ) {
        val updatedWorkoutExercisePlan = workoutExercisePlan.value?.copy(
            sets = if (sets.isBlank()) null else sets.toInt(),
            reps = if (reps.isBlank()) null else reps.toInt(),
            weightKg = if (weight.isBlank()) null else weight.toFloat(),
            timeInSec = if (timeInSec.isBlank()) null else timeInSec.toInt(),
            distanceInMeters = if (distanceInMeters.isBlank()) null else distanceInMeters.toInt()
        )

        _workoutExercisePlan.value = updatedWorkoutExercisePlan
    }

    suspend fun updateWorkoutExerciseComment(comment: String) {
        withContext(dispatcher) {
            updateWorkoutExerciseUseCase(workoutExercise.value!!.copy(comment = comment))
                .onFailure { _errors.emit(it) }
        }
    }

    suspend fun updateWorkoutExercisePlan() {
        withContext(dispatcher) {
            updateWorkoutExercisePlanUseCase(workoutExercisePlan.value!!)
                .onFailure { _errors.emit(it) }
        }
    }

    private fun loadWorkoutExercise(workoutExerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutExerciseUseCase(workoutExerciseId).collect { result ->
                result
                    .onSuccess { _workoutExercise.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    private fun loadWorkoutExercisePlan(workoutExerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutExercisePlanUseCase(workoutExerciseId).collect { result ->
                result
                    .onSuccess { _workoutExercisePlan.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }
}