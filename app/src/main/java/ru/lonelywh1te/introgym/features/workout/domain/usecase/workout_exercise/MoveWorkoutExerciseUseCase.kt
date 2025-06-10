package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.util.UUID

class MoveWorkoutExerciseUseCase(private val repository: WorkoutExerciseRepository) {
    suspend operator fun invoke(workoutId: UUID, from: Int, to: Int): Result<Unit> {
        if (from == to) return Result.Success(Unit)

        val workoutExercisesResult = repository.getWorkoutExercisesByWorkoutId(workoutId).first()

        when (workoutExercisesResult) {
            is Result.Success -> {
                val workoutExercises = workoutExercisesResult.data.toMutableList()

                if (from < 0 || to < 0 || from >= workoutExercises.size || to >= workoutExercises.size) {
                    throw IndexOutOfBoundsException("Invalid index: from = $from, to = $to, max size = ${workoutExercises.size}")
                }

                val item = workoutExercises.removeAt(from)
                workoutExercises.add(to, item)

                var moveResult: Result<Unit> = Result.Success(Unit)

                workoutExercises
                    .mapIndexed { index, workout -> workout.copy(order = index) }
                    .forEachIndexed { index, workoutExercise ->
                        if (workoutExercises[index].order != workoutExercise.order) {
                            val updateWorkoutResult = repository.updateWorkoutExercise(workoutExercise)

                            if (updateWorkoutResult is Result.Failure) {
                                moveResult = updateWorkoutResult
                                return@forEachIndexed
                            }
                        }
                    }

                return moveResult
            }
            is Result.Failure -> return workoutExercisesResult
            is Result.Loading -> return workoutExercisesResult
        }
    }
}