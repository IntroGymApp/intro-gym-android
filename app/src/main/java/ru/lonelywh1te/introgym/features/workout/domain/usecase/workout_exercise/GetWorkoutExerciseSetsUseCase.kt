package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository

class GetWorkoutExerciseSetsUseCase(
    private val repository: WorkoutExerciseSetRepository
) {
    operator fun invoke(workoutExerciseId: Long): Flow<Result<List<WorkoutExerciseSet>>> {
        return repository.getWorkoutExerciseSets(workoutExerciseId).map { result ->
            when(result) {
                is Result.Success -> Result.Success(result.data.sortedByDescending { it.createdAt })
                is Result.Failure -> result
                is Result.Loading -> result
            }
        }
    }
}