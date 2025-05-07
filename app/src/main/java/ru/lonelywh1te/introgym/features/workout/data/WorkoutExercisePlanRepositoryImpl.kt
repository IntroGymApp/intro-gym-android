package ru.lonelywh1te.introgym.features.workout.data

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.time.LocalDateTime

class WorkoutExercisePlanRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutExercisePlanRepository {
    override fun getWorkoutExercisePlans(workoutId: Long): Flow<Result<List<WorkoutExercisePlan>>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId)
            .flatMapLatest { workoutExercises ->
                val ids = workoutExercises.map { it.id }

                workoutExercisePlanDao.getWorkoutExercisePlans(ids)
                    .map<List<WorkoutExercisePlanEntity>, Result<List<WorkoutExercisePlan>>> { list ->
                        Result.Success(list.map { it.toWorkoutExercisePlan() })
                    }
            }
            .catch { e ->
                Log.e("WorkoutExercisePlanRepositoryImpl", "getWorkoutExercisePlans", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<Result<WorkoutExercisePlan>> {
        return workoutExercisePlanDao.getWorkoutExercisePlanById(workoutExerciseId)
            .map<WorkoutExercisePlanEntity, Result<WorkoutExercisePlan>> {
                Result.Success(it.toWorkoutExercisePlan())
            }
            .catch { e ->
                Log.e("WorkoutExercisePlanRepositoryImpl", "getWorkoutExercisePlanById", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Long> {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        return try {
            Result.Success(
                workoutExercisePlanDao.createWorkoutExercisePlan(workoutExercisePlanEntity)
            )
        } catch (e: Exception) {
            Log.e("WorkoutExercisePlanRepositoryImpl", "getWorkoutExercisePlanById", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit> {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        return try {
            Result.Success(
                workoutExercisePlanDao.updateWorkoutExercisePlan(workoutExercisePlanEntity)
            )
        } catch (e: Exception) {
            Log.e("WorkoutExercisePlanRepositoryImpl", "updateWorkoutExercisePlan", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun deleteWorkoutExercisePlan(id: Long): Result<Unit> {
        return try {
            Result.Success(
                workoutExercisePlanDao.deleteWorkoutExercisePlan(id)
            )
        } catch (e: Exception) {
            Log.e("WorkoutExercisePlanRepositoryImpl", "deleteWorkoutExercisePlan", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }
}