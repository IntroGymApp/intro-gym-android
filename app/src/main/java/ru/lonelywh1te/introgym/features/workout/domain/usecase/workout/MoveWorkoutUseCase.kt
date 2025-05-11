package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class MoveWorkoutUseCase(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(from: Int, to: Int): Result<Unit> {
        if (from == to) return Result.Success(Unit)

        val getWorkoutsResult = repository.getWorkouts()

        when (getWorkoutsResult) {
            is Result.Success -> {
                val workouts = getWorkoutsResult.data.toMutableList()

                if (from < 0 || to < 0 || from >= workouts.size || to >= workouts.size) {
                    throw IndexOutOfBoundsException("Invalid index: from = $from, to = $to, max size = ${workouts.size}")
                }

                val item = workouts.removeAt(from)
                workouts.add(to, item)

                var moveResult: Result<Unit> = Result.Success(Unit)

                workouts
                    .mapIndexed { index, workout -> workout.copy(order = index) }
                    .forEachIndexed { index, workout ->
                        if (workouts[index].order != workout.order) {
                            val updateWorkoutResult = repository.updateWorkout(workout)

                            if (updateWorkoutResult is Result.Failure) {
                                moveResult = updateWorkoutResult
                                return@forEachIndexed
                            }
                        }
                    }

                return moveResult
            }
            is Result.Failure -> return getWorkoutsResult
            is Result.Loading -> return getWorkoutsResult
        }
    }
}