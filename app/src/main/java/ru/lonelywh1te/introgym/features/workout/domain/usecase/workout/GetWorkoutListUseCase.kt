package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class GetWorkoutListUseCase(private val repository: WorkoutRepository) {
    operator fun invoke(): Flow<Result<List<WorkoutItem>>> {
        return repository.getWorkoutItems().map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.sortedBy { it.order })
                is Result.Failure -> result
                is Result.Loading -> result
            }
        }
    }
}