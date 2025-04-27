package ru.lonelywh1te.introgym.features.home.data

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate
import java.time.LocalDateTime

class WorkoutLogRepositoryImpl(
    private val db: MainDatabase,
    private val workoutLogDao: WorkoutLogDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutLogRepository {
    override suspend fun getWorkoutLogItemList(date: LocalDate): Result<List<WorkoutLogItem>> {
        return try {
            val workoutLogEntities = workoutLogDao.getWorkoutLogListByDate(date)
            Result.Success(mapWorkoutLogEntities(workoutLogEntities))

        } catch (e: Exception) {
            Log.e("WorkoutLogRepositoryImpl", "getWorkoutLogItemList", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
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
        return try {
            val workoutLogEntity = workoutLog.copy(
                lastUpdatedAt = LocalDateTime.now(),
            ).toWorkoutLogEntity()

            workoutLogDao.updateWorkoutLog(workoutLogEntity)

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("WorkoutLogRepositoryImpl", "updateWorkoutLog", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }

        }
    }

    override suspend fun addWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        return try {
            db.withTransaction {
                val workoutEntity = workoutDao.getWorkoutById(workoutLog.workoutId).first()
                val workoutExercises = workoutExerciseDao.getWorkoutExercisesById(workoutLog.workoutId).first()
                val workoutExercisePlans = workoutExercises.map { workoutExercisePlanDao.getWorkoutExercisePlanById(it.id).first() }

                val exercisesWithPlans = workoutExercises.associateWith { workoutExercise ->
                    workoutExercisePlans.find { workoutExercise.id == it.workoutExerciseId } ?: throw Exception("Cannot associate exercises with plans")
                }

                val workoutEntityToDate = workoutEntity.copy(
                    id = 0L,
                    isTemplate = false,
                    createdAt = LocalDateTime.now(),
                    lastUpdated = LocalDateTime.now(),
                    isSynchronized = false,
                )

                val createdWorkoutId = workoutDao.createWorkout(workoutEntityToDate)

                exercisesWithPlans.forEach {
                    val exercise = it.key
                    val plan = it.value

                    val newExerciseId = workoutExerciseDao.addWorkoutExercise(exercise.copy(
                        id = 0L,
                        workoutId = createdWorkoutId,
                        createdAt = LocalDateTime.now(),
                        lastUpdated = LocalDateTime.now(),
                        isSynchronized = false,
                    ))

                    workoutExercisePlanDao.createWorkoutExercisePlan(plan.copy(
                        id = 0L,
                        workoutExerciseId = newExerciseId,
                        createdAt = LocalDateTime.now(),
                        lastUpdated = LocalDateTime.now(),
                        isSynchronized = false,
                    ))
                }

                val countOfWorkoutLogAtDate = workoutLogDao.getCountOfWorkoutLogAtDate(workoutLog.date)
                val newWorkoutLog = workoutLog.copy(
                    workoutId = createdWorkoutId,
                    order = countOfWorkoutLogAtDate,
                    createdAt = LocalDateTime.now(),
                    lastUpdatedAt = LocalDateTime.now(),
                ).toWorkoutLogEntity()

                workoutLogDao.addWorkoutLog(newWorkoutLog)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("WorkoutLogRepositoryImpl", "addWorkoutLog", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }

        }
    }

    override suspend fun deleteWorkoutLog(workoutLog: WorkoutLog): Result<Unit> {
        return try {
            workoutDao.deleteWorkout(workoutLog.workoutId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("WorkoutLogRepositoryImpl", "deleteWorkoutLog", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }

        }
    }
}