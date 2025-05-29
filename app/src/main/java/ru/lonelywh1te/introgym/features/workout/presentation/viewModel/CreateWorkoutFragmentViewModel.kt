package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase

class CreateWorkoutFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout(name = "", isTemplate = true, order = -1))
    val workout: StateFlow<Workout> get() = _workout

    private val workoutExercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(listOf())
    private val workoutExercisePlans: MutableStateFlow<List<WorkoutExercisePlan>> = MutableStateFlow(listOf())

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem.Default>> = MutableStateFlow(listOf())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO

    private val _createWorkoutResult: MutableSharedFlow<Result<Unit>> = MutableSharedFlow()
    val createWorkoutResult: SharedFlow<Result<Unit>> get() = _createWorkoutResult

    fun createWorkout() {
        viewModelScope.launch (dispatcher) {
            val createWorkoutResult = createWorkoutUseCase(
                workout = _workout.value,
                exercises = workoutExercises.value,
                exercisePlans = workoutExercisePlans.value
            ).onFailure {
                if (it is AppError) errorDispatcher.dispatch(it)
            }

            _createWorkoutResult.emit(createWorkoutResult)
        }
    }

    fun addPickedExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()

            val newWorkoutExercise = createWorkoutExercise(exercise.id)
            val workoutExerciseItem = WorkoutExerciseItem.Default.create(exercise, newWorkoutExercise)
            val newWorkoutExercisePlan = createWorkoutExercisePlan(newWorkoutExercise.id)

            addWorkoutExercise(newWorkoutExercise)
            addWorkoutExercisePlan(newWorkoutExercisePlan)
            addWorkoutExerciseItem(workoutExerciseItem)
        }
    }

    private fun createWorkoutExercise(exerciseId: Long): WorkoutExercise {
        val temporaryId = workoutExercises.value.maxOfOrNull { it.id }?.plus(1) ?: 0L
        val newWorkoutExercise = WorkoutExercise(
            id = temporaryId,
            workoutId = _workout.value.id,
            exerciseId = exerciseId,
            order = workoutExercises.value.size,
            comment = ""
        )

        return newWorkoutExercise
    }

    private fun createWorkoutExercisePlan(workoutExerciseId: Long): WorkoutExercisePlan {
        val newWorkoutExercisePlan = WorkoutExercisePlan(workoutExerciseId = workoutExerciseId)
        return newWorkoutExercisePlan
    }

    private fun addWorkoutExercise(workoutExercise: WorkoutExercise) {
        workoutExercises.value = workoutExercises.value.plus(workoutExercise)
    }

    private fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        workoutExercisePlans.value = workoutExercisePlans.value.plus(workoutExercisePlan)
    }

    private fun addWorkoutExerciseItem(workoutExerciseItem: WorkoutExerciseItem.Default) {
        _workoutExerciseItems.value = _workoutExerciseItems.value.plus(workoutExerciseItem)
    }

    fun moveWorkoutExercise(from: Int, to: Int){
        val reorderedWorkoutExercises = workoutExercises.value.toMutableList()

        val item = reorderedWorkoutExercises.removeAt(from)
        reorderedWorkoutExercises.add(to, item)

        workoutExercises.value = reorderedWorkoutExercises.mapIndexed { index, workoutExercise -> workoutExercise.copy(order = index) }
    }

    fun moveWorkoutExerciseItem(from: Int, to: Int) {
        val reorderedWorkoutExerciseItems = _workoutExerciseItems.value.toMutableList()

        val item = reorderedWorkoutExerciseItems.removeAt(from)
        reorderedWorkoutExerciseItems.add(to, item)

        _workoutExerciseItems.value = reorderedWorkoutExerciseItems
    }

    fun deleteWorkoutExercise(position: Int) {
        val workoutExercisesCopy = workoutExercises.value.toMutableList()
        val workoutExercisePlansCopy = workoutExercisePlans.value.toMutableList()
        val workoutExerciseItemsCopy = _workoutExerciseItems.value.toMutableList()

        val deletedWorkoutExercise = workoutExercisesCopy.removeAt(position)
        workoutExercisePlansCopy.removeIf { it.workoutExerciseId == deletedWorkoutExercise.id }
        workoutExerciseItemsCopy.removeIf { it.workoutExerciseId == deletedWorkoutExercise.id }

        workoutExercises.value = workoutExercisesCopy.mapIndexed { index, workoutExercise -> workoutExercise.copy(order = index) }
        workoutExercisePlans.value = workoutExercisePlansCopy
        _workoutExerciseItems.value = workoutExerciseItemsCopy.mapIndexed { index, workoutExerciseItem -> workoutExerciseItem.copy(order = index) }
    }

    fun getWorkoutExerciseById(id: Long): WorkoutExercise {
        return workoutExercises.value.first { it.id == id }
    }

    fun getWorkoutExercisePlanById(workoutExerciseId: Long): WorkoutExercisePlan {
        return workoutExercisePlans.value.first { it.workoutExerciseId == workoutExerciseId }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        workoutExercisePlans.value = workoutExercisePlans.value.map { currentPlan ->
            if (currentPlan.workoutExerciseId == workoutExercisePlan.workoutExerciseId) workoutExercisePlan else currentPlan
        }
    }

    fun updateWorkoutExerciseComment(workoutExerciseId: Long, comment: String) {
        workoutExercises.value = workoutExercises.value.map { currentExercise ->
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