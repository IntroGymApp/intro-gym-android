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
import ru.lonelywh1te.introgym.core.result.Error
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
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.GetWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.DeleteWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsWithProgressUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.MoveWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StartWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.StopWorkoutUseCase

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
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val workout get() = _workout.asStateFlow()

    private val _workoutLog: MutableStateFlow<WorkoutLog?> = MutableStateFlow(null)
    val workoutLog get() = _workoutLog.asStateFlow()

    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> = _workoutLog
        .flatMapLatest { workoutLog ->
            var list: List<WorkoutExerciseItem> = emptyList()

            val state = workoutLog?.let { WorkoutLogState.get(workoutLog) }

            if (state == null || state is WorkoutLogState.NotStarted) {
                getWorkoutExerciseItemsUseCase(_workout.value!!.id).map { result ->
                    result
                        .onSuccess { list = it }
                        .onFailure { _errors.emit(it) }

                    list
                }
            } else {
                getWorkoutExerciseItemsWithProgressUseCase(_workout.value!!.id).map { result ->
                    result
                        .onSuccess { list = it }
                        .onFailure { _errors.emit(it) }

                    list
                }
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

   // private val _workoutResults: MutableStateFlow<WorkoutResult?> = MutableStateFlow(null)
    val workoutResults: StateFlow<WorkoutResult?> = _workoutLog
        .flatMapLatest { workoutLog ->
            var results: WorkoutResult? = null
            val state = workoutLog?.let { WorkoutLogState.get(workoutLog) }

            if (state != WorkoutLogState.Finished) return@flatMapLatest flowOf(null)

            getWorkoutResultsUseCase(workoutLog.workoutId).map { result ->
                result
                    .onSuccess { results = it }
                    .onFailure { _errors.emit(it) }

                results
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    private val _workoutDeleted: MutableSharedFlow<Unit> = MutableSharedFlow()
    val workoutDeleted get() = _workoutDeleted.asSharedFlow()

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collectLatest { result ->
                result
                    .onSuccess { _workout.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun loadWorkoutLog(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutLogUseCase(workoutId).collectLatest { result ->
                result
                    .onSuccess { _workoutLog.value = it }
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun startWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let {
                startWorkoutUseCase(it.id)
                    .onFailure { error -> _errors.emit(error) }
            }
        }
    }

    fun stopWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let {
                stopWorkoutUseCase(it.id)
                    .onFailure { error -> _errors.emit(error) }
            }
        }
    }

    fun updateWorkout(name: String, description: String) {
        viewModelScope.launch (dispatcher) {
            workout.value?.let { workout ->
                if (workout.name != name || workout.description != description) {
                    updateWorkoutUseCase(workout.copy(name = name, description = description))
                        .onFailure { _errors.emit(it) }
                }
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            workout.value?.let { workout ->
                deleteWorkoutUseCase(workout.id)
                    .onSuccess { _workoutDeleted.emit(it) }
                    .onFailure { _errors.emit(it) }
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
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun moveWorkoutExercise(from: Int, to: Int) {
        viewModelScope.launch {
            workout.value?.let { workout ->
                moveWorkoutExerciseUseCase(workout.id, from, to)
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun deleteWorkoutExercise(id: Long) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutExerciseUseCase(id)
                .onFailure { _errors.emit(it) }
        }
    }
}