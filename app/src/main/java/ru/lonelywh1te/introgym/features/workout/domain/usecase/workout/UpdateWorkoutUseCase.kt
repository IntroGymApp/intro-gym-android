package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class UpdateWorkoutUseCase(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(workout: Workout): Result<Unit> {
        return repository.updateWorkout(workout)
    }
}