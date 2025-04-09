package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import android.util.Log
import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class MoveWorkoutExerciseUseCase(private val repository: WorkoutExerciseRepository) {
    suspend operator fun invoke(workoutId: Long, from: Int, to: Int): Result<Unit> {
        Log.d("MoveWorkoutExercise", "$workoutId, $from -> $to")

        if (from == to) return Result.Success(Unit)

        val workoutExercisesResult = repository.getWorkoutExercisesById(workoutId).first()

        Log.d("MoveWorkoutExercise", "$workoutExercisesResult")

        Log.d("MoveWorkoutExercise", "BEFORE _________________________________")

        when (workoutExercisesResult) {
            is Result.Success -> {
                val workoutExercises = workoutExercisesResult.data.toMutableList()

                workoutExercises.forEach {
                    Log.d("MoveWorkoutExercise", "$it")
                }

                Log.d("MoveWorkoutExercise", "AFTER _________________________________")

                if (from < 0 || to < 0 || from >= workoutExercises.size || to >= workoutExercises.size) {
                    return Result.Failure(AppError.UNKNOWN)
                }

                val item = workoutExercises.removeAt(from)
                workoutExercises.add(to, item)

                var moveResult: Result<Unit> = Result.Success(Unit)

                workoutExercises
                    .mapIndexed { index, workout -> workout.copy(order = index) }
                    .forEachIndexed { index, workoutExercise ->
                        if (workoutExercises[index].order != workoutExercise.order) {
                            Log.d("MoveWorkoutExercise", "$workoutExercise")

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
            is Result.InProgress -> return workoutExercisesResult
        }
    }
}