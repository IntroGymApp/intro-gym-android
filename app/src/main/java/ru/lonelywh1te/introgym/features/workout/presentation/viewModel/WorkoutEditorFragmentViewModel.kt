package ru.lonelywh1te.introgym.features.workout.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
import ru.lonelywh1te.introgym.features.workout.domain.usecase.CreateWorkoutUseCase
import java.util.Map.entry

class WorkoutEditorFragmentViewModel(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout.empty(isTemplate = true))
    val workout: StateFlow<Workout> get() = _workout

    private val _workoutExercisesWithPlans: MutableStateFlow<MutableMap<WorkoutExercise, WorkoutExercisePlan>> = MutableStateFlow(
        mutableMapOf()
    )
    val workoutExercisesWithPlans: StateFlow<Map<WorkoutExercise, WorkoutExercisePlan>> get() = _workoutExercisesWithPlans

    private val _workoutExerciseItems: MutableStateFlow<List<WorkoutExerciseItem>> = MutableStateFlow(emptyList())
    val workoutExerciseItems: StateFlow<List<WorkoutExerciseItem>> get() = _workoutExerciseItems

    private val dispatcher = Dispatchers.IO

    private val _workoutSaved: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val workoutSaved: StateFlow<Boolean> get() = _workoutSaved

    fun saveWorkout() {
        viewModelScope.launch(dispatcher) {
            createWorkoutUseCase(_workout.value, _workoutExercisesWithPlans.value)
            _workoutSaved.value = true
        }
    }

    fun addWorkoutExercise(exerciseId: Long) {
        viewModelScope.launch(dispatcher) {
            val exercise = getExerciseUseCase(exerciseId).first()

            val newWorkoutExercise = WorkoutExercise.empty(
                id = _workoutExercisesWithPlans.value.size.toLong(),
                exerciseId = exerciseId,
                order = _workoutExercisesWithPlans.value.size
            )
            val newWorkoutExercisePlan = WorkoutExercisePlan.empty(workoutExerciseId = newWorkoutExercise.id)

            _workoutExercisesWithPlans.value[newWorkoutExercise] = newWorkoutExercisePlan

            addWorkoutExerciseItem(exercise, newWorkoutExercise)
        }
    }

    private fun addWorkoutExerciseItem(exercise: Exercise, workoutExercise: WorkoutExercise) {
        _workoutExerciseItems.value += WorkoutExerciseItem(
            workoutExerciseId = workoutExercise.id,
            name = exercise.name,
            imgFilename = exercise.imgFilename,
            order = workoutExercise.order,
        )
    }

    fun getWorkoutExerciseWithPlan(workoutExerciseId: Long): Pair<WorkoutExercise, WorkoutExercisePlan>? {
        val entry = _workoutExercisesWithPlans.value.entries.firstOrNull { it.key.id == workoutExerciseId }
        return entry?.let { entry.key to entry.value }
    }

    fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        _workoutExercisesWithPlans.value.forEach {
            if (it.key.id == workoutExercisePlan.workoutExerciseId) {
                _workoutExercisesWithPlans.value[it.key] = workoutExercisePlan
            }
        }
    }

    fun getWorkoutExerciseComment(workoutExerciseId: Long): String? {
        var comment = ""

        _workoutExercisesWithPlans.value.forEach {
            if (it.key.id == workoutExerciseId) comment = it.key.comment
        }

        return comment
    }

    fun updateWorkoutExerciseComment(workoutExerciseId: Long, comment: String) {
        _workoutExercisesWithPlans.value = _workoutExercisesWithPlans.value.map { (key, value) ->
            if (key.id == workoutExerciseId) {
                key.copy(comment = comment) to value
            } else {
                key to value
            }
        }.toMap().toMutableMap()
    }

    fun updateWorkoutName(name: String) {
        _workout.value = _workout.value.copy(name = name)
    }

    fun updateWorkoutDescription(description: String) {
        _workout.value = _workout.value.copy(description = description)
    }
}