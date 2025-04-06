package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class GetWorkoutByIdUseCase(
    private val repository: WorkoutRepository,
) {
    operator fun invoke(workoutId: Long): Flow<Result<Workout>> {
        return repository.getWorkoutById(workoutId)
    }
}