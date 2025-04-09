package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.WorkoutValidator
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.GetWorkoutExercisePlanUseCase

class WorkoutExercisePlanEditorFragmentViewModel(
    private val getWorkoutExercisePlanUseCase: GetWorkoutExercisePlanUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val validator: WorkoutValidator,
): ViewModel() {
    private val _workoutExercise: MutableStateFlow<WorkoutExercise?> = MutableStateFlow(null)
    val workoutExercise: StateFlow<WorkoutExercise?> = _workoutExercise

    private val _workoutExercisePlan: MutableStateFlow<WorkoutExercisePlan> = MutableStateFlow(WorkoutExercisePlan(workoutExerciseId = -1L))
    val workoutExercisePlan: StateFlow<WorkoutExercisePlan> = _workoutExercisePlan

    private val _exerciseName: MutableStateFlow<String> = MutableStateFlow("")
    val exerciseName: StateFlow<String> = _exerciseName

    private val _exerciseAnimFilename: MutableStateFlow<String> = MutableStateFlow("")
    val exerciseAnimFilename: StateFlow<String> = _exerciseAnimFilename

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: MutableSharedFlow<Error> get() = _errors

    val dispatcher = Dispatchers.IO

    fun setWorkoutExercise(workoutExercise: WorkoutExercise) {
        _workoutExercise.value = workoutExercise
    }

    fun setWorkoutExercisePlan(plan: WorkoutExercisePlan?) {
        if (plan != null) {
            _workoutExercisePlan.value = plan
        } else {
            _workoutExercise.value?.let { loadWorkoutExercisePlan(it.id) }
        }
    }

    fun updateWorkoutPlanExercise(
        sets: String,
        reps: String,
        weight: String,
        timeInSec: String,
        distanceInMeters: String
    ) {
        val updatedWorkoutExercisePlan = workoutExercisePlan.value.copy(
            sets = if (sets.isBlank()) null else sets.toInt(),
            reps = if (reps.isBlank()) null else reps.toInt(),
            weightKg = if (weight.isBlank()) null else weight.toFloat(),
            timeInSec = if (timeInSec.isBlank()) null else timeInSec.toInt(),
            distanceInMeters = if (distanceInMeters.isBlank()) null else distanceInMeters.toInt()
        )

        _workoutExercisePlan.value = updatedWorkoutExercisePlan
    }

    private fun loadWorkoutExercisePlan(workoutExerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutExercisePlanUseCase(workoutExerciseId).collect { result ->
                when(result) {
                    is Result.Success -> {
                        _workoutExercisePlan.value = result.data
                    }
                    is Result.Failure -> {
                        _errors.emit(result.error)
                    }
                    else -> { }
                }
            }
        }
    }


    private fun loadExerciseData(exerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()
            _exerciseName.value = exercise.name
            _exerciseAnimFilename.value = exercise.animFilename
        }
    }

    init {
        viewModelScope.launch (dispatcher) {
            _workoutExercise.collect { workoutExercise ->
                workoutExercise?.let { loadExerciseData(it.exerciseId) }
            }
        }
    }
}