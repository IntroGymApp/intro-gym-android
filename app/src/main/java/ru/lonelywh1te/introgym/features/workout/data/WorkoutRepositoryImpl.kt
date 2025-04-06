package ru.lonelywh1te.introgym.features.workout.data

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class WorkoutRepositoryImpl (
    private val workoutDao: WorkoutDao,
): WorkoutRepository {
    private val zoneOffset = ZoneOffset.UTC

    override fun getWorkoutItems(): Flow<Result<List<WorkoutItem>>> {
        return workoutDao.getWorkoutWithCountOfExercises()
            .map<List<WorkoutEntityWithCountOfExercises>, Result<List<WorkoutItem>>> { list ->
                Result.Success(list.map { entity -> entity.toWorkoutItem() })
            }
            .catch { e ->
                Log.e("WorkoutRepositoryImpl", "getWorkoutItems", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override fun getWorkouts(): Result<List<Workout>> {
        return try {
            Result.Success(workoutDao.getWorkouts().map { it.toWorkout() })
        } catch (e: Exception) {
            Log.e("WorkoutRepositoryImpl", "getWorkouts", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override fun getWorkoutById(workoutId: Long): Flow<Result<Workout>> {
        return workoutDao.getWorkoutById(workoutId)
            .filterNotNull()
            .map<WorkoutEntity, Result<Workout>> { Result.Success(it.toWorkout()) }
            .catch { e ->
                Log.e("WorkoutRepositoryImpl", "getWorkoutById", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override suspend fun createWorkout(workout: Workout): Result<Long> {
        val workoutEntity = workout.copy(
            createdAt = LocalDateTime.now(zoneOffset),
            lastUpdated = LocalDateTime.now(zoneOffset)
        ).toWorkoutEntity()

        return try {
            Result.Success(workoutDao.createWorkout(workoutEntity))
        } catch (e: Exception) {
            Log.e("WorkoutRepositoryImpl", "createWorkout", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun updateWorkout(workout: Workout): Result<Unit> {
        val workoutEntity = workout.copy(
            lastUpdated = LocalDateTime.now(zoneOffset)
        ).toWorkoutEntity()

        return try {
            Result.Success(workoutDao.updateWorkout(workoutEntity))
        } catch (e: Exception) {
            Log.e("WorkoutRepositoryImpl", "updateWorkout", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun deleteWorkout(id: Long): Result<Unit> {
        return try {
            Result.Success(workoutDao.deleteWorkout(id))
        } catch (e: Exception) {
            Log.e("WorkoutRepositoryImpl", "deleteWorkout", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun getCountOfWorkouts(): Result<Int> {
        return try {
            Result.Success(workoutDao.getCountOfWorkouts())
        } catch (e: Exception) {
            Log.e("WorkoutRepositoryImpl", "getCountOfWorkouts", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }
}