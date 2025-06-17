package ru.lonelywh1te.introgym.features.stats.domain.usecase

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.stats.domain.model.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.model.DistanceEntry
import ru.lonelywh1te.introgym.features.stats.domain.repository.StatsRepository
import java.time.LocalDate

class GetDistanceStatsUseCase(
    private val repository: StatsRepository,
) {
    operator fun invoke(period: StatsPeriod): Flow<Result<List<DistanceEntry>>> {
        return repository.getDistanceStats(period).map {
            when(it) {
                is Result.Success -> {
                    Result.Success(groupAndCalculateDistance(it.data, period))
                }
                else -> it
            }
        }
    }

    private fun groupAndCalculateDistance(list: List<DistanceEntry>, period: StatsPeriod): List<DistanceEntry> {
        return list
            .groupBy { if (period is StatsPeriod.Year) it.date.withDayOfMonth(1) else it.date }
            .mapValues { entry -> entry.value.sumOf { it.distance.toDouble() }.toInt() }
            .map { (date, distance) -> DistanceEntry(date, distance) }

    }
}