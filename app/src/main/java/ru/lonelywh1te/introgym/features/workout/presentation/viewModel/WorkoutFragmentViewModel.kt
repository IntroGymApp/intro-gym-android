package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.GetWorkoutExerciseItemsByWorkoutIdUseCase

class WorkoutFragmentViewModel(
    private val getWorkoutUseCase: GetWorkoutByIdUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val workout: StateFlow<Workout?> get() = _workout

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO

    fun getWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collect {
                _workout.value = it
            }
        }
    }


    init {
        viewModelScope.launch (dispatcher) {
            workout.collect {
                it?.let { workout ->
                    getWorkoutExerciseItemsByWorkoutIdUseCase(workout.id).collect {
                        _workoutExerciseItems.value = it
                    }
                }
            }
        }
    }
}