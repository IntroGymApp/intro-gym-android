package ru.lonelywh1te.introgym.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import java.time.LocalDate

interface WorkoutLogRepository {

    fun getWorkoutLogList(date: LocalDate): Result<Flow<List<WorkoutLogItem>>>

    fun updateWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    fun addWorkoutLog(date: LocalDate, workoutId: Long): Result<Long>

    fun deleteWorkoutLog(workoutLogId: Long): Result<Unit>

}