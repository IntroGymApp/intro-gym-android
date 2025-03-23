package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.GetWorkoutListUseCase

class WorkoutsFragmentViewModel(
    private val getWorkoutListUseCase: GetWorkoutListUseCase,
): ViewModel() {
    private val _workoutsList: MutableStateFlow<List<WorkoutItem>> = MutableStateFlow(listOf())
    val workoutList get() = _workoutsList

    private val dispatcher = Dispatchers.IO

    init {
        viewModelScope.launch (dispatcher){
            getWorkoutListUseCase().collect { list ->
                _workoutsList.value = list
            }
        }
    }
}