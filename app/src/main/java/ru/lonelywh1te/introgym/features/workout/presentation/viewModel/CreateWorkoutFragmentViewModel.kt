package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase

class CreateWorkoutFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout(name = "", isTemplate = true, order = -1))
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


    fun saveWorkout() {
        viewModelScope.launch (dispatcher) {
            val createWorkoutResult = createWorkoutUseCase(
                workout = _workout.value,
                exercises = _workoutExercises.value,
                exercisePlans = _workoutExercisePlans.value
            )

            _workoutSaveState.emit(createWorkoutResult)
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
                WorkoutExercisePlan(workoutExerciseId = newWorkoutExercise.id)
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

    fun getWorkoutExerciseById(id: Long): WorkoutExercise {
        return _workoutExercises.value.first { it.id == id }
    }

    fun getWorkoutExercisePlanById(workoutExerciseId: Long): WorkoutExercisePlan {
        return _workoutExercisePlans.value.first { it.workoutExerciseId == workoutExerciseId }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        _workoutExercisePlans.value = _workoutExercisePlans.value.map { currentPlan ->
            if (currentPlan.workoutExerciseId == workoutExercisePlan.workoutExerciseId) workoutExercisePlan else currentPlan
        }
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