package ru.lonelywh1te.introgym.features.home.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class WorkoutLogRepositoryImpl(
    private val db: MainDatabase,
    private val workoutLogDao: WorkoutLogDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutLogRepository {
    override fun getWorkoutLogItemList(date: LocalDate): Flow<Result<List<WorkoutLogItem>>> {
        return workoutLogDao.getWorkoutLogListByDate(date)
            .map<List<WorkoutLogEntity>, Result<List<WorkoutLogItem>>> { workoutLogEntities ->
                Result.Success(mapWorkoutLogEntities(workoutLogEntities))
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutLogByWorkoutId(workoutId: UUID): Flow<Result<WorkoutLog?>> {
        return workoutLogDao.getWorkoutLogByWorkoutId(workoutId)
            .map<WorkoutLogEntity?, Result<WorkoutLog?>> { workoutLogEntity ->
                Result.Success(workoutLogEntity?.toWorkoutLog())
            }
            .asSafeSQLiteFlow()
    }

    override suspend fun getWorkoutLogWithStartDateTime(): Result<WorkoutLog?> {
        return sqliteTryCatching { workoutLogDao.getWorkoutLogWithStartDateNotNull()?.toWorkoutLog() }
    }

    private fun mapWorkoutLogEntities(list: List<WorkoutLogEntity>): List<WorkoutLogItem> {
        return list.map { workoutLogEntity ->
            val workoutEntityWithCountOfExercises = workoutDao.getWorkoutWithCountOfExercises(workoutLogEntity.workoutId)

            val workoutEntity = workoutEntityWithCountOfExercises.workoutEntity
            val countOfExercises = workoutEntityWithCountOfExercises.countOfExercises

            workoutLogEntity.toWorkoutLogItem(workoutEntity, countOfExercises)
        }
    }

    override suspend fun updateWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        return sqliteTryCatching {
            val workoutLogEntity = workoutLog.copy(
                lastUpdatedAt = LocalDateTime.now(),
            ).toWorkoutLogEntity()

            workoutLogDao.updateWorkoutLog(workoutLogEntity)
        }
    }

    override suspend fun addWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        return sqliteTryCatching {
            db.withTransaction {
                val workoutEntity = workoutDao.getWorkoutById(workoutLog.workoutId).first()!!
                val workoutExercises = workoutExerciseDao.getWorkoutExercisesById(workoutLog.workoutId).first()
                val workoutExercisePlans = workoutExercises.map { workoutExercisePlanDao.getWorkoutExercisePlanById(it.id).first() }

                val exercisesWithPlans = workoutExercises.associateWith { workoutExercise ->
                    workoutExercisePlans.find { workoutExercise.id == it.workoutExerciseId } ?: throw Exception("Cannot associate exercises with plans")
                }

                val newWorkoutId = UUID.randomUUID()
                val workoutEntityToDate = workoutEntity.copy(
                    id = newWorkoutId,
                    isTemplate = false,
                    createdAt = LocalDateTime.now(),
                    lastUpdated = LocalDateTime.now(),
                    isSynchronized = false,
                )

                workoutDao.createWorkout(workoutEntityToDate)

                exercisesWithPlans.forEach {
                    val exercise = it.key
                    val plan = it.value

                    val newExerciseId = UUID.randomUUID()
                    workoutExerciseDao.addWorkoutExercise(exercise.copy(
                        id = newExerciseId,
                        workoutId = newWorkoutId,
                        createdAt = LocalDateTime.now(),
                        lastUpdated = LocalDateTime.now(),
                        isSynchronized = false,
                    ))

                    val newWorkoutExercisePlanId = UUID.randomUUID()
                    workoutExercisePlanDao.createWorkoutExercisePlan(plan.copy(
                        id = newWorkoutExercisePlanId,
                        workoutExerciseId = newExerciseId,
                        createdAt = LocalDateTime.now(),
                        lastUpdated = LocalDateTime.now(),
                        isSynchronized = false,
                    ))
                }

                val countOfWorkoutLogAtDate = workoutLogDao.getCountOfWorkoutLogAtDate(workoutLog.date)
                val newWorkoutLog = workoutLog.copy(
                    workoutId = newWorkoutId,
                    order = countOfWorkoutLogAtDate,
                    createdAt = LocalDateTime.now(),
                    lastUpdatedAt = LocalDateTime.now(),
                ).toWorkoutLogEntity()

                workoutLogDao.addWorkoutLog(newWorkoutLog)
            }
        }
    }

    override suspend fun deleteWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        return sqliteTryCatching { workoutDao.deleteWorkout(workoutLog.workoutId) }
    }

    override suspend fun getWorkoutLogDates(): Result<List<LocalDate>> {
        return sqliteTryCatching { workoutLogDao.getWorkoutLogDates() }
    }
}