package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDateTime

class StopWorkoutUseCase (
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(workoutId: Long): Result<Unit> {
        return when (val result = repository.getWorkoutLogByWorkoutId(workoutId).first()) {
            is Result.Success -> {
                result.data.let {
                    if (it == null) throw Exception("Workout has no WorkoutLog so cannot be finished")

                    val finishedWorkoutLog = it.copy(
                        endDateTime = LocalDateTime.now()
                    )

                    repository.updateWorkoutLog(finishedWorkoutLog)
                }
            }
            is Result.Failure -> result
            is Result.Loading -> result
        }
    }
}