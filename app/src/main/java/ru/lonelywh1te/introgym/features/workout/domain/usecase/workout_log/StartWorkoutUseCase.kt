package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.domain.error.WorkoutError
import java.time.LocalDateTime

class StartWorkoutUseCase (
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(workoutLog: WorkoutLog): Result<Unit> {
        repository.getWorkoutLogWithStartDateTime()
            .onSuccess {
                if (it != null) return Result.Failure(WorkoutError.WORKOUT_ALREADY_STARTED)
            }
            .onFailure {
                return Result.Failure(it)
            }

        val startedWorkoutLog = workoutLog.copy(
            startDateTime = LocalDateTime.now()
        )

        return repository.updateWorkoutLog(startedWorkoutLog)
    }
}