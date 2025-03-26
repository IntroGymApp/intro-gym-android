package ru.lonelywh1te.introgym.features.workout.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class GetWorkoutByIdUseCase(
    private val repository: WorkoutRepository,
) {
    operator fun invoke(workoutId: Long): Flow<Workout> {
        return repository.getWorkoutById(workoutId)
    }
}