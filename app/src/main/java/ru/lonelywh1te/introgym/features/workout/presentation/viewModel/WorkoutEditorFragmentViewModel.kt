package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.usecase.CreateWorkoutUseCase

class WorkoutEditorFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout.empty(isTemplate = true))
    val workout: StateFlow<Workout> get() = _workout

    private val _workoutExercisesWithPlans: MutableStateFlow<Map<WorkoutExercise, WorkoutExercisePlan>> = MutableStateFlow(emptyMap())

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO


    fun saveWorkout() {
        viewModelScope.launch(dispatcher) {
            createWorkoutUseCase(_workout.value, _workoutExercisesWithPlans.value)
        }
    }

    fun addWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()

            val newWorkoutExercise = WorkoutExercise.empty(
                id = _workoutExerciseItems.value.size.toLong(),
                exerciseId = exerciseId,
                order = _workoutExerciseItems.value.size
            )
            val newWorkoutExercisePlan = WorkoutExercisePlan.empty()

            _workoutExercisesWithPlans.update { it + (newWorkoutExercise to newWorkoutExercisePlan) }

            addWorkoutExerciseItem(exercise)
        }
    }

    private fun addWorkoutExerciseItem(exercise: Exercise) {
        _workoutExerciseItems.value += WorkoutExerciseItem(
            workoutExerciseId = _workoutExerciseItems.value.size.toLong(),
            name = exercise.name,
            imgFilename = exercise.imgFilename,
            order = _workoutExerciseItems.value.size,
        )
    }

    fun updateWorkoutName(name: String) {
        _workout.value = _workout.value.copy(name = name)
    }

    fun updateWorkoutDescription(description: String) {
        _workout.value = _workout.value.copy(description = description)
    }
}