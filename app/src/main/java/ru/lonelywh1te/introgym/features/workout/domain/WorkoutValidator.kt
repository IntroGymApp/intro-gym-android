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

        val validatePlansResult = validatePlans(plans)
        if (validatePlansResult is Result.Failure) return validatePlansResult

        return Result.Success(Unit)
    }

    private fun validateWorkout(workout: Workout): Result<Unit> {
        return if (workout.name.isBlank()) Result.Failure(WorkoutError.WORKOUT_EMPTY_NAME)
        else Result.Success(Unit)
    }

    private fun validateExerciseWithPlans(exercises: List<WorkoutExercise>, plans: List<WorkoutExercisePlan>): Result<Unit> {
        if (exercises.size != plans.size) return Result.Failure(WorkoutError.WORKOUT_EXERCISES_AND_PLANS_MISMATCH)
        return Result.Success(Unit)
    }

    private fun validateExercises(exercises: List<WorkoutExercise>): Result<Unit> {
        return when {
            exercises.isEmpty() -> Result.Failure(WorkoutError.WORKOUT_HAS_NO_EXERCISES)
            else -> Result.Success(Unit)
        }
    }

    // публичный, потому что будет использоваться отдельно
    fun validatePlans(plans: List<WorkoutExercisePlan>): Result<Unit> {
        plans.forEach { plan ->
            if (plan.isEmpty()) return Result.Failure(WorkoutError.EMPTY_EXERCISE_PLAN)
        }

        return Result.Success(Unit)
    }

    private fun WorkoutExercisePlan.isEmpty(): Boolean {
        return listOf(sets, reps, weightKg, timeInSec, distanceInMeters).all { it == null || it == 0 }
    }
}