package ru.lonelywh1te.introgym.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import java.time.LocalDate

interface WorkoutLogItemRepository {
    fun getWorkoutLogItems(date: LocalDate): Flow<Result<List<WorkoutLogItem>>>
}