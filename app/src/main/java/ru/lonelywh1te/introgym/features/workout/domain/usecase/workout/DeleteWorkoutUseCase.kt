package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository,
    private val reorderWorkoutsUseCase: ReorderWorkoutsUseCase,
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        val deleteWorkoutResult = repository.deleteWorkout(id)
        if (deleteWorkoutResult is Result.Failure) return deleteWorkoutResult

        val reorderWorkoutsResult = reorderWorkoutsUseCase()
        if (reorderWorkoutsResult is Result.Failure) return reorderWorkoutsResult

        return Result.Success(Unit)
    }
}