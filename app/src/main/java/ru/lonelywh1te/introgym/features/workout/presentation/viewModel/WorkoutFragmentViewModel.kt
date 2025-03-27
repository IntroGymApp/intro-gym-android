package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase

class WorkoutFragmentViewModel(
    private val getWorkoutUseCase: GetWorkoutByIdUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val workout: StateFlow<Workout?> get() = _workout

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val _workoutDeleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val workoutDeleted: StateFlow<Boolean> get() = _workoutDeleted

    private val dispatcher = Dispatchers.IO

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collect {
                _workout.value = it
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            _workout.value?.let {
                deleteWorkoutUseCase(it.id)
                _workout.value = null
            }

            _workoutDeleted.value = true
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