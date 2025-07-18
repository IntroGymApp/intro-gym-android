package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_log

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutLogRepository
import java.util.UUID

class GetWorkoutLogUseCase(
    private val repository: WorkoutLogRepository
) {
    operator fun invoke(workoutId: UUID): Flow<Result<WorkoutLog?>> {
        return repository.getWorkoutLogByWorkoutId(workoutId)
    }
}