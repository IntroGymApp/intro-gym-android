package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.domain.error.WorkoutError
import java.time.LocalDateTime
import java.util.UUID

class StartWorkoutUseCase (
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(workoutId: UUID): Result<Unit> {
        repository.getWorkoutLogWithStartDateTime()
            .onSuccess {
                if (it != null) return Result.Failure(WorkoutError.WorkoutAlreadyStarted())
            }
            .onFailure {
                return Result.Failure(it)
            }

        return when (val result = repository.getWorkoutLogByWorkoutId(workoutId).first()) {
            is Result.Success -> {
                result.data.let {
                    if (it == null) throw Exception("Workout has no WorkoutLog so cannot be started")

                    val startedWorkoutLog = it.copy(
                        startDateTime = LocalDateTime.now()
                    )

                    repository.updateWorkoutLog(startedWorkoutLog)
                }
            }
            is Result.Failure -> result
            is Result.Loading -> result
        }

    }
}