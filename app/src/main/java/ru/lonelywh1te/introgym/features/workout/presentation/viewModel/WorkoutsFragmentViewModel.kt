package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase

class WorkoutsFragmentViewModel(
    private val getWorkoutListUseCase: GetWorkoutListUseCase,
    private val moveWorkoutUseCase: MoveWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
): ViewModel() {
    private val _workoutsList: MutableStateFlow<List<WorkoutItem>> = MutableStateFlow(listOf())
    val workoutList get() = _workoutsList

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: SharedFlow<Error> get() = _errors

    private val dispatcher = Dispatchers.IO

    fun moveWorkout(from: Int, to: Int) {
        viewModelScope.launch (dispatcher) {
            moveWorkoutUseCase(from, to).onFailure { _errors.emit(it) }
        }
    }

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutUseCase(workoutId).onFailure { _errors.emit(it) }
        }
    }

    init {
        viewModelScope.launch (dispatcher){
            getWorkoutListUseCase().collect { result ->
                result
                    .onSuccess { _workoutsList.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }
}