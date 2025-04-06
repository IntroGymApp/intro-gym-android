package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.WorkoutValidator
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisePlansUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase

class WorkoutEditorFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val getWorkoutExercisesUseCase: GetWorkoutExercisesUseCase,
    private val getWorkoutExercisePlansUseCase: GetWorkoutExercisePlansUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout.empty(isTemplate = true))
    val workout: StateFlow<Workout> get() = _workout

    private val _workoutExercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(listOf())
    val workoutExercises: StateFlow<List<WorkoutExercise>> get() =_workoutExercises

    private val _workoutExercisePlans: MutableStateFlow<List<WorkoutExercisePlan>> = MutableStateFlow(listOf())
    val workoutExercisePlans: StateFlow<List<WorkoutExercisePlan>> get() = _workoutExercisePlans

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(listOf())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: MutableSharedFlow<Error> get() = _errors

    private val _workoutSaveState: MutableStateFlow<Result<Unit>?> = MutableStateFlow(null)
    val workoutSaveState: StateFlow<Result<Unit>?> get() = _workoutSaveState

    private fun initNewWorkout() {
        _workout.value = Workout.empty(isTemplate = true)
        _workoutExercises.value = emptyList()
        _workoutExercisePlans.value = emptyList()
    }

    fun setWorkout(workoutId: Long) {
        if (workoutId == -1L) {
            initNewWorkout()
        } else {
            loadWorkout(workoutId)
        }
    }

    private fun loadWorkout(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            val getWorkoutByIdResultDeferred = async { getWorkoutByIdUseCase(workoutId).first() }
            val getWorkoutExercisesResultDeferred = async { getWorkoutExercisesUseCase(workoutId).first() }
            val getWorkoutExerciseItemsDeferred = async { getWorkoutExerciseItemsByWorkoutIdUseCase(workoutId).first() }

            val workoutResult = getWorkoutByIdResultDeferred.await()
            val workoutExercisesResult = getWorkoutExercisesResultDeferred.await()
            val workoutExerciseItemsResult = getWorkoutExerciseItemsDeferred.await()

            if (workoutResult is Result.Success && workoutExercisesResult is Result.Success && workoutExerciseItemsResult is Result.Success) {
                _workout.value = workoutResult.data
                _workoutExercises.value = workoutExercisesResult.data
                _workoutExerciseItems.value = workoutExerciseItemsResult.data

                val getWorkoutExercisePlansResult = getWorkoutExercisePlansUseCase(_workoutExercises.value.map { it.id })

                when (getWorkoutExercisePlansResult) {
                    is Result.Success -> _workoutExercisePlans.value = getWorkoutExercisePlansResult.data
                    is Result.Failure -> errors.emit(getWorkoutExercisePlansResult.error)
                    else -> {}
                }

            } else {
                errors.emit(
                    when {
                        workoutResult is Result.Failure -> workoutResult.error
                        workoutExercisesResult is Result.Failure -> workoutExercisesResult.error
                        workoutExerciseItemsResult is Result.Failure -> workoutExerciseItemsResult.error
                        else -> AppError.UNKNOWN
                    }
                )
            }
        }
    }

    fun saveWorkout() {
        viewModelScope.launch (dispatcher) {
            if (_workout.value.id == -1L) {
                val createWorkoutResult = createWorkoutUseCase(
                    workout = _workout.value,
                    exercises = _workoutExercises.value,
                    exercisePlans = _workoutExercisePlans.value
                )

                _workoutSaveState.emit(createWorkoutResult)
            } else {
                val updateWorkoutResult = updateWorkoutUseCase(
                    workout = _workout.value,
                    exercises = _workoutExercises.value,
                    exercisePlans = _workoutExercisePlans.value
                )

                _workoutSaveState.emit(updateWorkoutResult)
            }
        }
    }

    fun addPickedWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()

            val newWorkoutExercise = WorkoutExercise(
                id = _workoutExercises.value.maxOfOrNull { it.id }?.plus(1) ?: 0L,
                workoutId = _workout.value.id,
                exerciseId = exerciseId,
                order = _workoutExercises.value.size,
                comment = ""
            )

            _workoutExercises.value = _workoutExercises.value.plus(newWorkoutExercise)
            _workoutExercisePlans.value = _workoutExercisePlans.value.plus(
                WorkoutExercisePlan.empty(workoutExerciseId = newWorkoutExercise.id)
            )

            addPickedWorkoutExerciseItem(exercise, newWorkoutExercise)
        }
    }

    private fun addPickedWorkoutExerciseItem(exercise: Exercise, workoutExercise: WorkoutExercise) {
        _workoutExerciseItems.value = _workoutExerciseItems.value.plus(
            WorkoutExerciseItem(
                workoutExerciseId = workoutExercise.id,
                name = exercise.name,
                imgFilename = exercise.imgFilename,
                order = workoutExercise.order
            )
        )
    }

    fun moveWorkoutExercise(from: Int, to: Int){
        val reorderedWorkoutExercises = _workoutExercises.value.toMutableList()

        val item = reorderedWorkoutExercises.removeAt(from)
        reorderedWorkoutExercises.add(to, item)

        _workoutExercises.value = reorderedWorkoutExercises.mapIndexed { index, workoutExercise -> workoutExercise.copy(order = index) }
    }

    fun moveWorkoutExerciseItem(from: Int, to: Int) {
        val reorderedWorkoutExerciseItems = _workoutExerciseItems.value.toMutableList()

        val item = reorderedWorkoutExerciseItems.removeAt(from)
        reorderedWorkoutExerciseItems.add(to, item)

        _workoutExerciseItems.value = reorderedWorkoutExerciseItems
    }

    fun deleteWorkoutExercise(position: Int) {
        val workoutExercises = _workoutExercises.value.toMutableList()
        val workoutExercisePlans = _workoutExercisePlans.value.toMutableList()
        val workoutExerciseItems = _workoutExerciseItems.value.toMutableList()

        val deletedWorkoutExercise = workoutExercises.removeAt(position)
        workoutExercisePlans.removeIf { it.workoutExerciseId == deletedWorkoutExercise.id }
        workoutExerciseItems.removeIf { it.workoutExerciseId == deletedWorkoutExercise.id }

        _workoutExercises.value = workoutExercises.mapIndexed { index, workoutExercise -> workoutExercise.copy(order = index) }
        _workoutExercisePlans.value = workoutExercisePlans
        _workoutExerciseItems.value = workoutExerciseItems.mapIndexed { index, workoutExerciseItem -> workoutExerciseItem.copy(order = index) }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        _workoutExercisePlans.value = _workoutExercisePlans.value.map { currentPlan ->
            if (currentPlan.workoutExerciseId == workoutExercisePlan.workoutExerciseId) workoutExercisePlan else currentPlan
        }
    }

    fun getWorkoutExerciseComment(workoutExerciseId: Long): String {
        return _workoutExercises.value
            .firstOrNull { it.id == workoutExerciseId }
            ?.comment ?: ""
    }

    fun updateWorkoutExerciseComment(workoutExerciseId: Long, comment: String) {
        _workoutExercises.value = _workoutExercises.value.map { currentExercise ->
            if (currentExercise.id == workoutExerciseId) currentExercise.copy(comment = comment) else currentExercise
        }
    }

    fun updateWorkoutName(name: String) {
        _workout.value = _workout.value.copy(name = name)
    }

    fun updateWorkoutDescription(description: String) {
        _workout.value = _workout.value.copy(description = description)
    }
}