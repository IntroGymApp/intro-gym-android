package ru.lonelywh1te.introgym.features.stats.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.model.MuscleEntry
import ru.lonelywh1te.introgym.features.stats.domain.repository.StatsRepository

class GetMusclesStatsUseCase(
    private val repository: StatsRepository,
    private val exerciseCategoriesUseCase: GetExerciseCategoriesUseCase,
) {
    suspend operator fun invoke(period: StatsPeriod): Flow<Result<List<MuscleEntry>>> {
        return repository.getMusclesStats(period).map {
            when(it) {
                is Result.Success -> {
                    Result.Success(groupAndSumMuscleData(it.data))
                }
                else -> it
            }
        }
    }

    private suspend fun groupAndSumMuscleData(list: List<MuscleEntry>): List<MuscleEntry> {
        val groupedData =  list
            .groupBy { it.categoryName }
            .mapValues { (_, list) -> list.sumOf { it.effort.toDouble() }.toInt() }

        val allCategories = exerciseCategoriesUseCase.invoke().first().map { it.name }

        return allCategories.map { category ->
            val effort = groupedData[category] ?: 0
            MuscleEntry(categoryName = category, effort = effort)
        }
    }
}