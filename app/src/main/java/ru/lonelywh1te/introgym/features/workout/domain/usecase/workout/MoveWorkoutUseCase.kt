package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class MoveWorkoutUseCase(
    private val repository: WorkoutRepository,
    private val reorderWorkoutsUseCase: ReorderWorkoutsUseCase,
) {
    suspend operator fun invoke(from: Int, to: Int): Result<Unit> {
        if (from == to) return Result.Success(Unit)

        val getWorkoutsResult = repository.getWorkouts()

        when (getWorkoutsResult) {
            is Result.Success -> {
                val workouts = getWorkoutsResult.data.toMutableList()

                if (from < 0 || to < 0 || from >= workouts.size || to >= workouts.size) return Result.Failure(AppError.UNKNOWN)

                val item = workouts.removeAt(from)
                workouts.add(to, item)

                return reorderWorkoutsUseCase(workouts)
            }
            is Result.Failure -> return getWorkoutsResult
            is Result.InProgress -> return getWorkoutsResult
        }
    }
}