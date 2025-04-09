package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_plan

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository

class GetWorkoutExercisePlanUseCase(
    private val repository: WorkoutExercisePlanRepository
) {
    operator fun invoke(workoutExerciseId: Long): Flow<Result<WorkoutExercisePlan>> {
        return repository.getWorkoutExercisePlanById(workoutExerciseId)
    }
}