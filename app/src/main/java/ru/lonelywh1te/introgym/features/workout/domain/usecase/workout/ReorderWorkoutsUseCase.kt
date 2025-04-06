package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import android.util.Log
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class ReorderWorkoutsUseCase(private val repository: WorkoutRepository) {
    suspend operator fun invoke(list: List<Workout>? = null): Result<Unit> {
        val getWorkoutsResult = if (list != null) Result.Success(list) else repository.getWorkouts()

        return when (getWorkoutsResult) {
            is Result.Success -> {
                val workouts = getWorkoutsResult.data

                Log.d("ReorderWorkoutsUseCase", "$workouts")

                var updateResult: Result<Unit> = Result.Success(Unit)

                workouts
                    .mapIndexed { index, workout -> workout.copy(order = index) }
                    .forEach {
                        val updateWorkoutResult = repository.updateWorkout(it)

                        if (updateWorkoutResult is Result.Failure) {
                            updateResult = updateWorkoutResult
                            return@forEach
                        }
                    }


                updateResult
            }

            is Result.Failure -> getWorkoutsResult
            is Result.InProgress -> getWorkoutsResult
        }
    }
}