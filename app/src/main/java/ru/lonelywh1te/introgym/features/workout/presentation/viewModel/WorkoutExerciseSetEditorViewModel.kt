package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.getOrNull
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_set.DeleteWorkoutExerciseSetUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_set.GetWorkoutExerciseSetUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_set.UpdateWorkoutExerciseSetUseCase
import java.util.UUID

class WorkoutExerciseSetEditorViewModel(
    private val getWorkoutExerciseSetUseCase: GetWorkoutExerciseSetUseCase,
    private val updateWorkoutExerciseSetUseCase: UpdateWorkoutExerciseSetUseCase,
    private val deleteWorkoutExerciseSetUseCase: DeleteWorkoutExerciseSetUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _workoutExerciseSetId: MutableStateFlow<UUID?> = MutableStateFlow(null)

    val workoutExerciseSet: StateFlow<WorkoutExerciseSet?> = _workoutExerciseSetId
        .filterNotNull()
        .mapLatest { id ->
            getWorkoutExerciseSetUseCase(id)
                .onFailure { errorDispatcher.dispatch(it) }
                .getOrNull()
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _changesSaved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val changesSaved get() = _changesSaved.asSharedFlow()

    fun setWorkoutExerciseSetId(id: UUID) {
        _workoutExerciseSetId.value = id
    }

    fun saveWorkoutExerciseSet(
        reps: String,
        weight: String,
        timeInSec: String,
        distanceInMeters: String,
        effort: Effort?,
    ) {
        viewModelScope.launch {
            Log.d("ViewModel", "$reps | $weight | $timeInSec | $timeInSec | $effort")

            workoutExerciseSet.value?.copy(
                reps = if (reps.isBlank()) null else reps.toIntOrNull(),
                weightKg = if (weight.isBlank()) null else weight.toFloatOrNull(),
                timeInSeconds = if (timeInSec.isBlank()) null else timeInSec.toIntOrNull(),
                distanceInMeters = if (distanceInMeters.isBlank()) null else distanceInMeters.toIntOrNull(),
                effort = effort
            )?.let { updatedWorkoutExerciseSet ->
                updateWorkoutExerciseSetUseCase(updatedWorkoutExerciseSet)
                    .onSuccess { _changesSaved.emit(Unit) }
                    .onFailure { errorDispatcher.dispatch(it) }
            }
        }
    }

    fun deleteWorkoutExerciseSet() {
        viewModelScope.launch(dispatcher) {
            _workoutExerciseSetId.value?.let { id ->
                deleteWorkoutExerciseSetUseCase(id)
                    .onSuccess { _changesSaved.emit(Unit) }
                    .onFailure { errorDispatcher.dispatch(it) }
            }

        }
    }
}