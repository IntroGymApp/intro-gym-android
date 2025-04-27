package ru.lonelywh1te.introgym.features.home.domain.usecase

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository

class AddWorkoutLogUseCase(
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(workoutLog: WorkoutLog): Result<Unit> {
        return repository.addWorkoutLog(workoutLog)
    }
}