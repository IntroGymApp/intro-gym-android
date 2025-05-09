package ru.lonelywh1te.introgym.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import java.time.LocalDate

interface WorkoutLogRepository {

    fun getWorkoutLogItemList(date: LocalDate): Flow<Result<List<WorkoutLogItem>>>

    fun getWorkoutLogByWorkoutId(workoutId: Long): Flow<Result<WorkoutLog?>>

    suspend fun getWorkoutLogWithStartDateTime(): Result<WorkoutLog?>

    suspend fun updateWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun addWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun deleteWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun getWorkoutLogDates(): Result<List<LocalDate>>

}