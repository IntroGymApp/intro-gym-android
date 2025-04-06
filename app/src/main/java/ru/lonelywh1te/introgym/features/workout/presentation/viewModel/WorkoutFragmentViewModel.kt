package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.core.result.Result

class WorkoutFragmentViewModel(
    private val getWorkoutUseCase: GetWorkoutByIdUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val workout: StateFlow<Workout?> get() = _workout

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: MutableSharedFlow<Error> get() = _errors

    private val _workoutDeleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val workoutDeleted: StateFlow<Boolean> get() = _workoutDeleted

    private val dispatcher = Dispatchers.IO

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collect { result ->
                when (result) {
                    is Result.Success -> _workout.value = result.data
                    is Result.Failure -> _errors.emit(result.error)
                    else -> {}
                }
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            _workout.value?.let {
                val deleteWorkoutResult = deleteWorkoutUseCase(it.id)
                if (deleteWorkoutResult is Result.Failure) _errors.emit(deleteWorkoutResult.error)

                _workout.value = null
            }

            _workoutDeleted.value = true
        }
    }


    init {
        viewModelScope.launch (dispatcher) {
            workout
                .filterNotNull()
                .flatMapLatest { workout ->
                    getWorkoutExerciseItemsByWorkoutIdUseCase(workout.id)
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> _workoutExerciseItems.value = result.data
                        is Result.Failure -> _errors.emit(result.error)
                        else -> {}
                    }
                }

        }
    }
}