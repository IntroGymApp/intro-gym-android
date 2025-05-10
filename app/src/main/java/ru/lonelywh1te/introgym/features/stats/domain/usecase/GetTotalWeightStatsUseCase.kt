package ru.lonelywh1te.introgym.features.stats.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.model.WeightEntry
import ru.lonelywh1te.introgym.features.stats.domain.repository.StatsRepository
import java.time.LocalDate

class GetTotalWeightStatsUseCase(
    private val repository: StatsRepository,
) {
    operator fun invoke(period: StatsPeriod): Flow<Result<List<WeightEntry>>> {
        return repository.getTotalWeightStats(period).map { result ->
            var resultData: Result<List<WeightEntry>>? = null

            result
                .onSuccess { list ->
                    resultData = Result.Success(groupAndCalculateTotalWeight(list, period))
                }
                .onFailure {
                    resultData = Result.Failure(it)
                }

            resultData ?: throw Exception("resultData is null")
        }
    }

    private fun groupAndCalculateTotalWeight(list: List<WeightEntry>, period: StatsPeriod): List<WeightEntry> {
        val groupedData = list
            .groupBy { it.date }
            .mapValues { entry -> entry.value.sumOf { it.weight.toDouble() }.toFloat() }

        val allDates = generateDatesForPeriod(period)

        val resultMap = if (period is StatsPeriod.Year) {
            allDates
                .associateWith { groupedData[it] ?: 0f }
                .toList()
                .groupBy { it.first.withDayOfMonth(1) }
                .mapValues { entry -> entry.value.sumOf { it.second.toDouble() }.toFloat() }
        } else {
            allDates.associateWith { groupedData[it] ?: 0f }
        }

        return resultMap
            .map { (date, weight) -> WeightEntry(date, weight) }
            .sortedBy { it.date }
    }

    private fun generateDatesForPeriod(period: StatsPeriod): List<LocalDate> {
        return when (period) {
            is StatsPeriod.Week -> {
                val startDate = period.startLocalDate
                (0 until 7).map { startDate.plusDays(it.toLong()) }
            }
            is StatsPeriod.Month -> {
                val startDate = period.startLocalDate
                val endDate = period.endLocalDate
                generateSequence(startDate) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(endDate) }
                    .toList()
            }
            is StatsPeriod.Year -> {
                val startDate = period.startLocalDate
                val endDate = period.endLocalDate
                generateSequence(startDate) { it.plusDays(1) }
                    .takeWhile { !it.isAfter(endDate) }
                    .toList()
            }
        }
    }
}