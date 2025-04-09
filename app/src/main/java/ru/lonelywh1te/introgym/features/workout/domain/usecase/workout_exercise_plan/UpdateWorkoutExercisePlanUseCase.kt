package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository

class UpdateWorkoutExercisePlanUseCase(private val repository: WorkoutExercisePlanRepository) {
    suspend operator fun invoke(workoutExercisePlan: WorkoutExercisePlan): Result<Unit> {
        return repository.updateWorkoutExercisePlan(workoutExercisePlan)
    }
}