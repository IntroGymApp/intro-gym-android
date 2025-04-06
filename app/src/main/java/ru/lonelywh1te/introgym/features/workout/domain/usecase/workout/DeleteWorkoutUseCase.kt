package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteWorkoutWithReorder(id)
    }
}