package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class GetWorkoutExerciseItemsUseCase(
    private val repository: WorkoutExerciseRepository,
) {
    operator fun invoke(workoutId: Long): Flow<Result<List<WorkoutExerciseItem>>> {
        return repository.getWorkoutExerciseItems(workoutId).map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.sortedBy { it.order })
                is Result.Failure -> result
                is Result.Loading -> result
            }
        }
    }
}