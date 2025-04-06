package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
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
    val errors: MutableSharedFlow<Error> get() = _errors

    private val dispatcher = Dispatchers.IO

    fun moveWorkout(from: Int, to: Int) {
        viewModelScope.launch (dispatcher) {
            val moveWorkoutResult = moveWorkoutUseCase(from, to)

            if (moveWorkoutResult is Result.Failure) _errors.emit(moveWorkoutResult.error)
        }
    }

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            val deleteWorkoutResult = deleteWorkoutUseCase(workoutId)

            if (deleteWorkoutResult is Result.Failure) _errors.emit(deleteWorkoutResult.error)
        }
    }

    init {
        viewModelScope.launch (dispatcher){
            getWorkoutListUseCase().collect { result ->
                when (result) {
                    is Result.Success -> _workoutsList.value = result.data
                    is Result.Failure -> _errors.emit(result.error)
                    else -> {}
                }
            }
        }
    }
}