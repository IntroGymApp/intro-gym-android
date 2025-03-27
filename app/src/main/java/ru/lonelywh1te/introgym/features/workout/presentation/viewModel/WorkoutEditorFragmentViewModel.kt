package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.CreateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.GetWorkoutByIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.UpdateWorkoutUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExerciseItemsByWorkoutIdUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisePlanUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise.GetWorkoutExercisesUseCase

class WorkoutEditorFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val getWorkoutExercisesUseCase: GetWorkoutExercisesUseCase,
    private val getWorkoutExercisePlanUseCase: GetWorkoutExercisePlanUseCase,
    private val getWorkoutExerciseItemsByWorkoutIdUseCase: GetWorkoutExerciseItemsByWorkoutIdUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout.empty(isTemplate = true))
    val workout: StateFlow<Workout?> get() = _workout

    private val _workoutExercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(listOf())
    val workoutExercises: StateFlow<List<WorkoutExercise>> get() =_workoutExercises

    private val _workoutExercisePlans: MutableStateFlow<List<WorkoutExercisePlan>> = MutableStateFlow(listOf())
    val workoutExercisePlans: StateFlow<List<WorkoutExercisePlan>> get() = _workoutExercisePlans

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(listOf())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO

    private val _workoutSaved: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val workoutSaved: StateFlow<Boolean> get() = _workoutSaved

    fun setWorkout(workoutId: Long) {
        viewModelScope.launch(dispatcher) {
            if (workoutId == -1L) {
                _workout.value = Workout.empty(isTemplate = true)
                _workoutExercises.value = listOf()
                _workoutExercisePlans.value = listOf()
            } else {
                val workoutDeferred = async { getWorkoutByIdUseCase(workoutId).first() }
                val workoutExercisesDeferred = async { getWorkoutExercisesUseCase(workoutId).first() }
                val workoutExerciseItemsDeferred = async { getWorkoutExerciseItemsByWorkoutIdUseCase(workoutId).first() }

                val workout = workoutDeferred.await()
                _workout.value = workout

                val workoutExercises = workoutExercisesDeferred.await()
                _workoutExercises.value = workoutExercises

                val plans = workoutExercises.map { workoutExercise ->
                    async { getWorkoutExercisePlanUseCase(workoutExercise.id).first() }
                }.awaitAll()

                _workoutExercisePlans.value = plans

                val exerciseItems = workoutExerciseItemsDeferred.await()
                _workoutExerciseItems.value = exerciseItems
            }
        }
    }

    fun saveWorkout() {
        viewModelScope.launch (dispatcher) {
            if (_workout.value.id == -1L) {
                createWorkoutUseCase(
                    workout = _workout.value,
                    exercises = _workoutExercises.value,
                    exercisePlans = _workoutExercisePlans.value
                )
            } else {
                updateWorkoutUseCase(
                    workout = _workout.value,
                    exercises = _workoutExercises.value,
                    exercisePlans = _workoutExercisePlans.value
                )
            }

            _workoutSaved.value = true
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

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        Log.d("WorkoutEditorVM", _workoutExercisePlans.value.toString())
        Log.d("WorkoutEditorVM", "${workoutExercisePlans.value.map { currentPlan ->
            if (currentPlan.workoutExerciseId == workoutExercisePlan.workoutExerciseId) workoutExercisePlan else currentPlan
        }}")

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

    init {
        Log.d("ViewModel", "ViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "ViewModel cleared")
    }
}