package ru.lonelywh1te.introgym.features.workout.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository

class GetWorkoutExercisePlanUseCase(
    private val repository: WorkoutExercisePlanRepository
) {
    operator fun invoke(workoutExerciseId: Long): Flow<WorkoutExercisePlan> {
        return repository.getWorkoutExercisePlanById(workoutExerciseId)
    }
}