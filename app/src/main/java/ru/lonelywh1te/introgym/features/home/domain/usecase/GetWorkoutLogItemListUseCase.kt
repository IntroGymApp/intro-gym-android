package ru.lonelywh1te.introgym.features.home.domain.usecase

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate

class GetWorkoutLogItemListUseCase(
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(date: LocalDate): Result<List<WorkoutLogItem>> {
        return repository.getWorkoutLogItemList(date)
    }
}