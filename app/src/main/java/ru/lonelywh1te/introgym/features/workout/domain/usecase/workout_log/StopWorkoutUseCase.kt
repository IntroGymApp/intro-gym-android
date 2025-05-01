package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDateTime

class StopWorkoutUseCase (
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(workoutLog: WorkoutLog): Result<Unit> {
        val finishedWorkoutLog = workoutLog.copy(
            endDateTime = LocalDateTime.now()
        )

        return repository.updateWorkoutLog(finishedWorkoutLog)
    }
}