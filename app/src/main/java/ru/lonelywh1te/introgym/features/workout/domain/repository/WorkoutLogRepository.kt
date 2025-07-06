package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import java.time.LocalDate
import java.util.UUID

interface WorkoutLogRepository {

    fun getWorkoutLogByWorkoutId(workoutId: UUID): Flow<Result<WorkoutLog?>>

    suspend fun getWorkoutLogWithStartDateTime(): Result<WorkoutLog?>

    suspend fun updateWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun addWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun deleteWorkoutLog(workoutLog: WorkoutLog): Result<Unit>

    suspend fun getWorkoutLogDates(): Result<List<LocalDate>>

}