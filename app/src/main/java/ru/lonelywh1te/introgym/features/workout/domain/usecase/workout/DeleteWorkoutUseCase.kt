package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.util.UUID

class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(id: UUID): Result<Unit> {
        return repository.deleteWorkoutWithReorder(id)
    }
}