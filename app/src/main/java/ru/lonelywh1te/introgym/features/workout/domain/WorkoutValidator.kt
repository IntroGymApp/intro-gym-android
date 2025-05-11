package ru.lonelywh1te.introgym.features.workout.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.error.WorkoutError
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

object WorkoutValidator {

    fun validate(workout: Workout, exercises: List<WorkoutExercise>, plans: List<WorkoutExercisePlan>): Result<Unit> {
        val validateWorkoutResult = validateWorkout(workout)
        if (validateWorkoutResult is Result.Failure) return validateWorkoutResult

        val validateExerciseWithPlansResult = validateExerciseWithPlans(exercises, plans)
        if (validateExerciseWithPlansResult is Result.Failure) return validateExerciseWithPlansResult

        val validateExercisesResult = validateExercises(exercises)
        if (validateExercisesResult is Result.Failure) return validateExercisesResult

        return Result.Success(Unit)
    }

    private fun validateWorkout(workout: Workout): Result<Unit> {
        return if (workout.name.isBlank()) Result.Failure(WorkoutError.WorkoutEmptyName())
        else Result.Success(Unit)
    }

    private fun validateExerciseWithPlans(exercises: List<WorkoutExercise>, plans: List<WorkoutExercisePlan>): Result<Unit> {
        if (exercises.size != plans.size) return Result.Failure(WorkoutError.WorkoutExercisesAndPlansMismatch())
        return Result.Success(Unit)
    }

    private fun validateExercises(exercises: List<WorkoutExercise>): Result<Unit> {
        return when {
            exercises.isEmpty() -> Result.Failure(WorkoutError.WorkoutHasNoExercises())
            else -> Result.Success(Unit)
        }
    }
}