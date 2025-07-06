package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.MoveWorkoutUseCase
import java.util.UUID

class WorkoutsFragmentViewModel(
    private val getWorkoutListUseCase: GetWorkoutListUseCase,
    private val moveWorkoutUseCase: MoveWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val _workoutsList: MutableStateFlow<UIState<List<WorkoutItem>>?> = MutableStateFlow(null)
    val workoutList get() = _workoutsList

    private val dispatcher = Dispatchers.IO

    fun moveWorkout(from: Int, to: Int) {
        viewModelScope.launch (dispatcher) {
            moveWorkoutUseCase(from, to).onFailure { errorDispatcher.dispatch(it) }
        }
    }

    fun deleteWorkout(workoutId: UUID) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutUseCase(workoutId).onFailure { errorDispatcher.dispatch(it) }
        }
    }

    init {
        viewModelScope.launch (dispatcher){
            getWorkoutListUseCase().collect { result ->
                _workoutsList.value = result
                    .onFailure { errorDispatcher.dispatch(it) }
                    .toUIState()
            }
        }
    }
}