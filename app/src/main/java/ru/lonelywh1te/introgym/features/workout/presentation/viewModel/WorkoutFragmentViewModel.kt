package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.getOrNull
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState
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
import ru.lonelywh1te.introgym.features.workout.presentation.error.WorkoutErrorStringMessageProvider
import ru.lonelywh1te.introgym.features.workout.presentation.state.WorkoutFragmentUiData
import java.time.LocalTime
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

    private val workoutResult: StateFlow<Result<Workout>> = _workoutId
        .filterNotNull()
        .flatMapLatest { workoutId ->
            getWorkoutUseCase(workoutId)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    private val workoutLogResult: StateFlow<Result<WorkoutLog?>> = _workoutId
        .filterNotNull()
        .flatMapLatest { workoutId ->
            getWorkoutLogUseCase(workoutId)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    private val workoutExerciseItemsResult: StateFlow<Result<List<WorkoutExerciseItem>>> = _workoutId
        .filterNotNull()
        .flatMapLatest { workoutId ->
            getWorkoutExerciseItemsUseCase(workoutId)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    private val workoutExerciseItemsWithProgressResult: StateFlow<Result<List<WorkoutExerciseItem>?>> = workoutLogResult
        .flatMapLatest { workoutLogResult ->
            val workoutLog = workoutLogResult.getOrNull()

            if (workoutLog == null || workoutLog.state is WorkoutLogState.NotStarted) return@flatMapLatest flowOf(Result.Success(null))
            getWorkoutExerciseItemsWithProgressUseCase(workoutLog.workoutId)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    private val workoutResults: StateFlow<Result<WorkoutResult?>> = workoutLogResult
        .flatMapLatest { workoutLogResult ->
            val workoutLog = workoutLogResult.getOrNull()

            if (workoutLog == null || workoutLog.state !is WorkoutLogState.Finished) return@flatMapLatest flowOf(Result.Success(null))

            getWorkoutResultsUseCase(workoutLog.workoutId)
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Loading)

    val workoutFragmentState: StateFlow<UIState<WorkoutFragmentUiData>?> = combine(
        workoutResult,
        workoutLogResult,
        workoutExerciseItemsResult,
        workoutExerciseItemsWithProgressResult,
        workoutResults,
    ) { workout, workoutLog, workoutExerciseItems, workoutExerciseItemsWithProgress, workoutResult ->

        val data = listOf(workout, workoutLog, workoutExerciseItems, workoutExerciseItemsWithProgress, workoutResult)

        when {
            data.any { it is Result.Loading } -> UIState.Loading
            data.any { it is Result.Failure } -> {
                val error = (data.find { it is Result.Failure } as Result.Failure).error
                errorDispatcher.dispatch(error)

                UIState.Failure(error)
            }
            data.all { it is Result.Success } -> {
                val workoutFragmentUiData = WorkoutFragmentUiData(
                    workout = (workout as Result.Success).data,
                    workoutLog = (workoutLog as Result.Success).data,
                    workoutExerciseItems = if (workoutLog.data == null || workoutLog.data.state is WorkoutLogState.NotStarted) {
                        (workoutExerciseItems as Result.Success).data
                    } else {
                        (workoutExerciseItemsWithProgress as Result.Success).data
                    },
                    workoutResult = (workoutResult as Result.Success).data,
                )

                UIState.Success(workoutFragmentUiData)
            }
            else -> {
                Log.d("WorkoutFragmentViewModel", "state: ${data.map { it.javaClass.simpleName }}")
                val error = AppError.Unknown(IllegalStateException("Unknown workoutFragmentState"))
                errorDispatcher.dispatch(error)

                UIState.Failure(error)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _workoutExecutionTime: MutableStateFlow<LocalTime?> = MutableStateFlow(null)
    val workoutExecutionTime get() = _workoutExecutionTime.asStateFlow()

    private val _workoutDeleted: MutableSharedFlow<Unit> = MutableSharedFlow()
    val workoutDeleted get() = _workoutDeleted.asSharedFlow()

    fun setWorkoutId(workoutId: UUID) {
        _workoutId.value = workoutId
    }

    fun startWorkout() {
        viewModelScope.launch (dispatcher) {
            _workoutId.value?.let { workoutId ->
                startWorkoutUseCase(workoutId)
                    .onFailure { error -> errorDispatcher.dispatch(error, WorkoutErrorStringMessageProvider.get(error)) }
            }
        }
    }

    fun stopWorkout() {
        viewModelScope.launch (dispatcher) {
            _workoutId.value?.let { workoutId ->
                stopWorkoutUseCase(workoutId)
                    .onFailure { error -> errorDispatcher.dispatch(error) }
            }
        }
    }

    fun updateWorkout(name: String, description: String) {
        viewModelScope.launch (dispatcher) {
            workoutResult.value.getOrNull()?.let { workout ->
                if (workout.name != name || workout.description != description) {

                    updateWorkoutUseCase(workout.copy(name = name, description = description))
                        .onFailure { errorDispatcher.dispatch(it) }
                }
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            _workoutId.value?.let { workoutId ->
                deleteWorkoutUseCase(workoutId)
                    .onSuccess { _workoutDeleted.emit(it) }
                    .onFailure { errorDispatcher.dispatch(it) }
            }
        }
    }

    fun addWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            _workoutId.value?.let { workoutId ->
                val newWorkoutExercise = WorkoutExercise(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    order = workoutExerciseItemsResult.value?.getOrNull()?.size ?: 0,
                )

                addWorkoutExerciseUseCase(newWorkoutExercise)
                    .onFailure { errorDispatcher.dispatch(it) }
            }
        }
    }

    fun moveWorkoutExercise(from: Int, to: Int) {
        viewModelScope.launch {
            _workoutId.value?.let { workoutId ->
                moveWorkoutExerciseUseCase(workoutId, from, to)
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

    fun setExecutionTimeFlow(flow: Flow<LocalTime>) {
        viewModelScope.launch {
            flow.collect {
                _workoutExecutionTime.value = it
            }
        }
    }
}