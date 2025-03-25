package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

class WorkoutExercisePlanEditorFragmentViewModel(
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val _exerciseName: MutableStateFlow<String> = MutableStateFlow("")
    val exerciseName: StateFlow<String> = _exerciseName

    private val _exerciseAnimFilename: MutableStateFlow<String> = MutableStateFlow("")
    val exerciseAnimFilename: StateFlow<String> = _exerciseAnimFilename

    private val _workoutPlanExercise: MutableStateFlow<WorkoutExercisePlan> = MutableStateFlow(WorkoutExercisePlan.empty())
    val workoutPlanExercise: StateFlow<WorkoutExercisePlan> = _workoutPlanExercise

    val dispatcher = Dispatchers.IO

    fun getExerciseData(exerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()
            _exerciseName.value = exercise.name
            _exerciseAnimFilename.value = exercise.animFilename
        }
    }

    fun updateSets(setsString: String) {
        val sets = if (setsString.isBlank()) null else setsString.toInt()
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(sets = sets)
    }

    fun updateReps(repsString: String) {
        val reps = if (repsString.isBlank()) null else repsString.toInt()
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(reps = reps)
    }

    fun updateWeight(weightString: String) {
        val weight = if (weightString.isBlank()) null else weightString.toFloat()
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(weightKg = weight)
    }

    fun updateTimeInSec(timeInSecString: String) {
        val timeInSec = if (timeInSecString.isBlank()) null else timeInSecString.toInt()
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(timeInSec = timeInSec)
    }

    fun updateDistanceInMeters(distanceInMetersString: String) {
        val distanceInMeters = if (distanceInMetersString.isBlank()) null else distanceInMetersString.toInt()
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(distanceInMeters = distanceInMeters)
    }

    fun setWorkoutExerciseId(workoutExerciseId: Long) {
        _workoutPlanExercise.value = _workoutPlanExercise.value.copy(workoutExerciseId = workoutExerciseId)
    }

    fun getWorkoutExercisePlan(workoutExerciseId: Long) {
        TODO("Not yet implemented")
    }
}