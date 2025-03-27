package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase

class WorkoutsFragmentViewModel(
    private val getWorkoutListUseCase: GetWorkoutListUseCase,
    private val moveWorkoutUseCase: MoveWorkoutUseCase,
): ViewModel() {
    private val _workoutsList: MutableStateFlow<List<WorkoutItem>> = MutableStateFlow(listOf())
    val workoutList get() = _workoutsList

    private val dispatcher = Dispatchers.IO

    fun moveWorkout(from: Int, to: Int) {
        viewModelScope.launch (dispatcher) {
            moveWorkoutUseCase(from, to)
        }
    }

    init {
        viewModelScope.launch (dispatcher){
            getWorkoutListUseCase().collect { list ->
                _workoutsList.value = list
            }
        }
    }
}