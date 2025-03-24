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

    val dispatcher = Dispatchers.IO

    fun getExerciseData(exerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()
            _exerciseName.value = exercise.name
            _exerciseAnimFilename.value = exercise.animFilename
        }
    }
}