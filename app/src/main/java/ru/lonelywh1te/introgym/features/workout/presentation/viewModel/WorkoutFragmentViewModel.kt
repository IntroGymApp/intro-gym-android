package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.AddWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.DeleteWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.MoveWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.UpdateWorkoutExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan.UpdateWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log.GetWorkoutLogUseCase

class WorkoutFragmentViewModel(
    private val getWorkoutUseCase: GetWorkoutByIdUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,

    private val addWorkoutExerciseUseCase: AddWorkoutExerciseUseCase,
    private val updateWorkoutExerciseUseCase: UpdateWorkoutExerciseUseCase,
    private val deleteWorkoutExerciseUseCase: DeleteWorkoutExerciseUseCase,
    private val moveWorkoutExerciseUseCase: MoveWorkoutExerciseUseCase,
    private val getWorkoutExercisesUseCase: GetWorkoutExercisesUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,

    private val updateWorkoutExercisePlanUseCase: UpdateWorkoutExercisePlanUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val workout get() = _workout.asStateFlow()

    private val workoutExercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(emptyList())

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems get() = _workoutExerciseItems.asStateFlow()

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    private val _workoutDeleted: MutableSharedFlow<Unit> = MutableSharedFlow()
    val workoutDeleted get() = _workoutDeleted.asSharedFlow()

    private val dispatcher = Dispatchers.IO

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collect { result ->
                result
                    .onSuccess { _workout.value = it }
                    .onFailure { _errors.emit(it) }
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
            _workout.value?.let { workout ->
                deleteWorkoutUseCase(workout.id)
                    .onSuccess { _workoutDeleted.emit(it) }
                    .onFailure { _errors.emit(it) }

                _workout.value = null
            }
        }
    }

    fun getWorkoutExerciseById(id: Long): WorkoutExercise {
        return workoutExercises.value.first { it.id == id }
    }

    fun addWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            workout.value?.let { workout ->
                val newWorkoutExercise = WorkoutExercise(
                    workoutId = workout.id,
                    exerciseId = exerciseId,
                    order = workoutExercises.value.size,
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

    fun moveWorkoutExerciseItem(from: Int, to: Int) {
        val reorderedWorkoutExerciseItems = _workoutExerciseItems.value.toMutableList()

        val item = reorderedWorkoutExerciseItems.removeAt(from)
        reorderedWorkoutExerciseItems.add(to, item)

        _workoutExerciseItems.value = reorderedWorkoutExerciseItems
    }

    fun deleteWorkoutExercise(id: Long) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutExerciseUseCase(id)
                .onFailure { _errors.emit(it) }
        }
    }

    fun updateWorkoutExerciseComment(workoutExerciseId: Long, comment: String) {
        viewModelScope.launch(dispatcher) {
            val workoutExercise = workoutExercises.value.first { it.id == workoutExerciseId}

            if (workoutExercise.comment != comment) {
                updateWorkoutExerciseUseCase(workoutExercise.copy(comment = comment))
                    .onFailure { _errors.emit(it) }
            }
        }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        viewModelScope.launch (dispatcher) {
            updateWorkoutExercisePlanUseCase(workoutExercisePlan)
                .onFailure { _errors.emit(it) }
        }
    }

    init {
        viewModelScope.launch(dispatcher) {
            workout
                .filterNotNull()
                .flatMapLatest { workout ->
                    getWorkoutExerciseItemsByWorkoutIdUseCase(workout.id)
                }
                .collect { result ->
                    result
                        .onSuccess { _workoutExerciseItems.value = it }
                        .onFailure { _errors.emit(it) }
                }
        }

        viewModelScope.launch(dispatcher) {
            workout
                .filterNotNull()
                .flatMapLatest { workout ->
                    getWorkoutExercisesUseCase(workout.id)
                }
                .collect { result ->
                    result
                        .onSuccess { workoutExercises.value = it }
                        .onFailure { _errors.emit(it) }
                }
        }
    }
}