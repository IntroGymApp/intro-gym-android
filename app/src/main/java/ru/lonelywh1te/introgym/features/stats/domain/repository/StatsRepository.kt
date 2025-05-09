package ru.lonelywh1te.introgym.features.stats.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import java.time.LocalDate

interface StatsRepository {

    fun getTotalWeightStats(period: StatsPeriod): Flow<Result<List<Pair<LocalDate, Float>>>>

}