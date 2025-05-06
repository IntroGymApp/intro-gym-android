package ru.lonelywh1te.introgym.features.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate

class GetWorkoutLogItemListUseCase(
    private val repository: WorkoutLogRepository,
) {
    operator fun invoke(date: LocalDate): Flow<Result<List<WorkoutLogItem>>> {
        return repository.getWorkoutLogItemList(date).map { result ->
            when(result) {
                is Result.Success -> Result.Success(sortWorkoutLogItems(result.data))
                is Result.Failure -> result
                is Result.Loading -> result
            }
        }
    }

    private fun sortWorkoutLogItems(workoutLogItems: List<WorkoutLogItem>): List<WorkoutLogItem> {
        return workoutLogItems.sortedBy { item ->
            when (item.state) {
                WorkoutLogState.InProgress -> 0
                WorkoutLogState.NotStarted -> 1
                WorkoutLogState.Finished -> 2
            }
        }
    }
}