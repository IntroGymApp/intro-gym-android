package ru.lonelywh1te.introgym.features.stats.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.stats.domain.model.DistanceEntry
import ru.lonelywh1te.introgym.features.stats.domain.model.MuscleEntry
import ru.lonelywh1te.introgym.features.stats.domain.model.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.model.WeightEntry

interface StatsRepository {

    fun getTotalWeightStats(period: StatsPeriod): Flow<Result<List<WeightEntry>>>

    fun getMusclesStats(period: StatsPeriod): Flow<Result<List<MuscleEntry>>>

    fun getDistanceStats(period: StatsPeriod): Flow<Result<List<DistanceEntry>>>

}