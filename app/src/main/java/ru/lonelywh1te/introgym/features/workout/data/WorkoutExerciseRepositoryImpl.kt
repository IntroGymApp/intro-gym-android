package ru.lonelywh1te.introgym.features.workout.data

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutExerciseWithExerciseInfo
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.time.LocalDateTime

class WorkoutExerciseRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
): WorkoutExerciseRepository {

    override fun getWorkoutExerciseItems(workoutId: Long): Flow<Result<List<WorkoutExerciseItem>>> {
        return workoutExerciseDao.getWorkoutExercisesWithExerciseInfo(workoutId)
            .map<List<WorkoutExerciseWithExerciseInfo>, Result<List<WorkoutExerciseItem>>> {
                list -> Result.Success(list.map { it.toWorkoutExerciseItem() })
            }
            .catch { e ->
                Log.e("WorkoutExerciseRepositoryImpl", "getWorkoutExerciseItems", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override fun getWorkoutExercisesById(workoutId: Long): Flow<Result<List<WorkoutExercise>>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId)
            .map<List<WorkoutExerciseEntity>, Result<List<WorkoutExercise>>> {
                list -> Result.Success(list.map { it.toWorkoutExercise() })
            }
            .catch { e ->
                Log.e("WorkoutExerciseRepositoryImpl", "getWorkoutExercisesById", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Result<Long> {
        val workoutExerciseEntity = workoutExercise.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return try {
            Result.Success(workoutExerciseDao.addWorkoutExercise(workoutExerciseEntity))
        } catch (e: Exception) {
            Log.e("WorkoutExerciseRepositoryImpl", "getWorkouts", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit> {
        val workoutExerciseEntity = workoutExercise.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return try {
            Result.Success(workoutExerciseDao.updateWorkoutExercise(workoutExerciseEntity))
        } catch (e: Exception) {
            Log.e("WorkoutExerciseRepositoryImpl", "getWorkouts", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun deleteWorkoutExercise(id: Long): Result<Unit> {
        return try {
            Result.Success(workoutExerciseDao.deleteWorkoutExercise(id))
        } catch (e: Exception) {
            Log.e("WorkoutExerciseRepositoryImpl", "getWorkouts", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }
}