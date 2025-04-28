package ru.lonelywh1te.introgym.features.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate

class GetWorkoutLogItemListUseCase(
    private val repository: WorkoutLogRepository,
) {
    operator fun invoke(date: LocalDate): Flow<Result<List<WorkoutLogItem>>> {
        return repository.getWorkoutLogItemList(date)
    }
}