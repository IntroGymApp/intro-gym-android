package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.util.UUID

class GetWorkoutByIdUseCase(
    private val repository: WorkoutRepository,
) {
    operator fun invoke(workoutId: UUID): Flow<Result<Workout>> {
        return repository.getWorkoutById(workoutId)
    }
}