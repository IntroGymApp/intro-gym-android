package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
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
    val workout: StateFlow<Workout?> get() = _workout

    private val workoutExercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(emptyList())

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: MutableSharedFlow<Error> get() = _errors

    private val _workoutDeleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val workoutDeleted: StateFlow<Boolean> get() = _workoutDeleted

    private val dispatcher = Dispatchers.IO

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            getWorkoutUseCase(workoutId).collect { result ->
                when (result) {
                    is Result.Success -> _workout.value = result.data
                    is Result.Failure -> _errors.emit(result.error)
                    else -> {}
                }
            }
        }
    }

    fun updateWorkout(name: String, description: String) {
        viewModelScope.launch (dispatcher) {
            workout.value?.let { workout ->
                if (workout.name != name || workout.description != description) {
                    val result = updateWorkoutUseCase(workout.copy(name = name, description = description))
                    if (result is Result.Failure) _errors.emit(result.error)
                }
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch (dispatcher) {
            _workout.value?.let {
                val result = deleteWorkoutUseCase(it.id)
                if (result is Result.Failure) _errors.emit(result.error)

                _workout.value = null
            }

            _workoutDeleted.value = true
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

                val result = addWorkoutExerciseUseCase(newWorkoutExercise)
                if (result is Result.Failure) _errors.emit(result.error)
            }
        }
    }

    fun moveWorkoutExercise(from: Int, to: Int) {
        viewModelScope.launch {
            workout.value?.let { workout ->
                val result = moveWorkoutExerciseUseCase(workout.id, from, to)
                if (result is Result.Failure) _errors.emit(result.error)

                Log.d("WorkoutFragmentVM", "$result")
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
            val result = deleteWorkoutExerciseUseCase(id)
            if (result is Result.Failure) _errors.emit(result.error)
        }
    }

    fun updateWorkoutExerciseComment(workoutExerciseId: Long, comment: String) {
        viewModelScope.launch(dispatcher) {
            val workoutExercise = workoutExercises.value.first { it.id == workoutExerciseId}

            if (workoutExercise.comment != comment) {
                val result = updateWorkoutExerciseUseCase(workoutExercise.copy(comment = comment))
                if (result is Result.Failure) _errors.emit(result.error)
            }
        }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        viewModelScope.launch (dispatcher) {
            val result = updateWorkoutExercisePlanUseCase(workoutExercisePlan)
            if (result is Result.Failure) _errors.emit(result.error)
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
                    when (result) {
                        is Result.Success -> _workoutExerciseItems.value = result.data
                        is Result.Failure -> _errors.emit(result.error)
                        else -> {}
                    }
                }
        }

        viewModelScope.launch(dispatcher) {
            workout
                .filterNotNull()
                .flatMapLatest { workout ->
                    getWorkoutExercisesUseCase(workout.id)
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> workoutExercises.value = result.data
                        is Result.Failure -> _errors.emit(result.error)
                        else -> {}
                    }
                }
        }
    }
}