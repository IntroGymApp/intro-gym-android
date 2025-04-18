package ru.lonelywh1te.introgym.features.home.data

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate

class WorkoutLogRepositoryImpl: WorkoutLogRepository {
    override fun getWorkoutLogList(date: LocalDate): Result<Flow<List<WorkoutLogItem>>> {
        TODO("Not yet implemented")
    }

    override fun updateWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun addWorkoutLog(date: LocalDate, workoutId: Long): Result<Long> {
        TODO("Not yet implemented")
    }

    override fun deleteWorkoutLog(workoutLogId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}