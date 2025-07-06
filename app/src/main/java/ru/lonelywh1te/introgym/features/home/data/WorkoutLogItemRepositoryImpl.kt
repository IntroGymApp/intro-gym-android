package ru.lonelywh1te.introgym.features.home.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogItemRepository
import java.time.LocalDate

class WorkoutLogItemRepositoryImpl(
    private val workoutLogDao: WorkoutLogDao,
    private val workoutDao: WorkoutDao,
): WorkoutLogItemRepository {
    override fun getWorkoutLogItems(date: LocalDate): Flow<Result<List<WorkoutLogItem>>> {
        return workoutLogDao.getWorkoutLogListByDate(date)
            .map<List<WorkoutLogEntity>, Result<List<WorkoutLogItem>>> { workoutLogEntities ->
                Result.Success(mapWorkoutLogEntities(workoutLogEntities))
            }
            .onStart { emit(Result.Loading) }
            .asSafeSQLiteFlow()
    }

    private fun mapWorkoutLogEntities(list: List<WorkoutLogEntity>): List<WorkoutLogItem> {
        return list.map { workoutLogEntity ->
            val workoutEntityWithCountOfExercises = workoutDao.getWorkoutWithCountOfExercises(workoutLogEntity.workoutId)

            val workoutEntity = workoutEntityWithCountOfExercises.workoutEntity
            val countOfExercises = workoutEntityWithCountOfExercises.countOfExercises

            workoutLogEntity.toWorkoutLogItem(workoutEntity, countOfExercises)
        }
    }
}