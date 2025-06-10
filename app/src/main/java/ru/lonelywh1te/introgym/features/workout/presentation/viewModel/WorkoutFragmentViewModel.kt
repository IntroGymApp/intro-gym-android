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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutResult
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutResultsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StartWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StopWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.DeleteWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsWithProgressUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.MoveWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.GetWorkoutLogUseCase
import java.util.UUID

class WorkoutFragmentViewModel(
    private val getWorkoutUseCase: GetWorkoutByIdUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,

    private val addWorkoutExerciseUseCase: AddWorkoutExerciseUseCase,
    private val deleteWorkoutExerciseUseCase: DeleteWorkoutExerciseUseCase,
    private val moveWorkoutExerciseUseCase: MoveWorkoutExerciseUseCase,

    private val getWorkoutExerciseItemsUseCase: GetWorkoutExerciseItemsUseCase,
    private val getWorkoutExerciseItemsWithProgressUseCase: GetWorkoutExerciseItemsWithProgressUseCase,

    private val startWorkoutUseCase: StartWorkoutUseCase,
    private val stopWorkoutUseCase: StopWorkoutUseCase,
    private val getWorkoutLogUseCase: GetWorkoutLogUseCase,

    private val getWorkoutResultsUseCase: GetWorkoutResultsUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _workoutId: MutableStateFlow<UUID?> = MutableStateFlow(null)

    val workout: StateFlow<Workout?> = _workoutId
        .filterNotNull()
        .flatMapLatest { workoutId ->
            var workout: Workout? = null

            getWorkoutUseCase(workoutId).map { result ->
                result
                    .onSuccess { workout = it }
                    .onFailure { errorDispatcher.dispatch(it) }

                workout
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val workoutLog: StateFlow<WorkoutLog?> = _workoutId
        .filterNotNull()
        .flatMapLatest { workoutId ->
            var workoutLog: WorkoutLog? = null

            getWorkoutLogUseCase(workoutId).map { result ->
                result
                    .onSuccess { workoutLog = it }
                    .onFailure { errorDispatcher.dispatch(it) }

                workoutLog
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> = workoutLog
        .flatMapLatest { workoutLog ->
            var list: List<WorkoutExerciseItem> = emptyList()

            val state = workoutLog?.let { WorkoutLogState.get(workoutLog) }

            if (state == null || state is WorkoutLogState.NotStarted) {
                getWorkoutExerciseItemsUseCase(_workoutId.value!!).map { result ->
                    result
                        .onSuccess { list = it }
                        .onFailure { errorDispatcher.dispatch(it) }

                    list
                }
            } else {
                getWorkoutExerciseItemsWithProgressUseCase(_workoutId.value!!).map { result ->
                    result
                        .onSuccess { list = it }
                        .onFailure { errorDispatcher.dispatch(it) }

                    list
                }
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val workoutResults: StateFlow<WorkoutResult?> = workoutLog
        .flatMapLatest { workoutLog ->
            var results: WorkoutResult? = null
            val state = workoutLog?.let { WorkoutLogState.get(workoutLog) }

            if (state != WorkoutLogState.Finished) return@flatMapLatest flowOf(null)

            getWorkoutResultsUseCase(workoutLog.workoutId).map { result ->
                result
                    .onSuccess { results = it }
                    .onFailure { errorDispatcher.dispatch(it) }

                results
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _workoutDeleted: MutableSharedFlow<Unit> = MutableSharedFlow()
    val workoutDeleted get() = _workoutDeleted.asSharedFlow()

    fun setWorkoutId(workoutId: UUID) {
        _workoutId.value = workoutId
    }

    fun startWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let {
                startWorkoutUseCase(it.id)
                    .onFailure { error -> errorDispatcher.dispatch(error) }
            }
        }
    }

    fun stopWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let {
                stopWorkoutUseCase(it.id)
                    .onFailure { error -> errorDispatcher.dispatch(error) }
            }
        }
    }

    fun updateWorkout(name: String, description: String) {
        viewModelScope.launch (dispatcher) {
            workout.value?.let { workout ->
                if (workout.name != name || workout.description != description) {
                    updateWorkoutUseCase(workout.copy(name = name, description = description))
                        .onFailure { errorDispatcher.dispatch(it) }
                }
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let { workout ->
                deleteWorkoutUseCase(workout.id)
                    .onSuccess {
                        _workoutId.value = null
                        _workoutDeleted.emit(it)
                    }
                    .onFailure {
                        errorDispatcher.dispatch(it)
                    }
            }
        }
    }

    fun addWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            workout.value?.let { workout ->
                val newWorkoutExercise = WorkoutExercise(
                    workoutId = workout.id,
                    exerciseId = exerciseId,
                    order = workoutExerciseItems.value.size,
                )

                addWorkoutExerciseUseCase(newWorkoutExercise)
                    .onFailure { errorDispatcher.dispatch(it) }
            }
        }
    }

    fun moveWorkoutExercise(from: Int, to: Int) {
        viewModelScope.launch {
            workout.value?.let { workout ->
                moveWorkoutExerciseUseCase(workout.id, from, to)
                    .onFailure { errorDispatcher.dispatch(it) }
            }
        }
    }

    fun deleteWorkoutExercise(id: UUID) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutExerciseUseCase(id)
                .onFailure { errorDispatcher.dispatch(it) }
        }
    }
}