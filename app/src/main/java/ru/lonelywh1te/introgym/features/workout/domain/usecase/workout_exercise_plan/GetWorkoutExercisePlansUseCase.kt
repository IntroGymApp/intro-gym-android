package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan

import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository

class GetWorkoutExercisePlansUseCase(
    private val repository: WorkoutExercisePlanRepository,
) {
    suspend operator fun invoke(workoutExerciseIds: List<Long>): Result<List<WorkoutExercisePlan>> {
        val plans = mutableListOf<WorkoutExercisePlan>()

        for (id in workoutExerciseIds) {
            val result = repository.getWorkoutExercisePlanById(id).first()
            when (result) {
                is Result.Success -> plans.add(result.data)
                is Result.Failure -> return Result.Failure(result.error)
                else -> {}
            }
        }

        return Result.Success(plans)
    }
}