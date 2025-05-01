package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.GetWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.StartWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.StopWorkoutUseCase

class WorkoutControlViewModel(
    private val getWorkoutLogUseCase: GetWorkoutLogUseCase,
    private val startWorkoutUseCase: StartWorkoutUseCase,
    private val stopWorkoutUseCase: StopWorkoutUseCase,
): ViewModel() {
    private val _workoutLog: MutableStateFlow<WorkoutLog?> = MutableStateFlow(null)
    val workoutLog get() = _workoutLog.asStateFlow()

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    private val dispatcher = Dispatchers.IO

    fun loadWorkoutLog(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutLogUseCase(workoutId).collect { result ->
                result
                    .onSuccess { _workoutLog.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun startWorkout() {
        viewModelScope.launch (dispatcher) {
            _workoutLog.value?.let {
                startWorkoutUseCase(it)
                    .onFailure { error -> _errors.emit(error) }
            }
        }
    }

    fun stopWorkout() {
        viewModelScope.launch (dispatcher) {
            _workoutLog.value?.let {
                stopWorkoutUseCase(it)
                    .onFailure { error -> _errors.emit(error) }
            }
        }
    }
}