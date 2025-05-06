package ru.lonelywh1te.introgym.features.workout.data

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository
import java.time.LocalDateTime

class WorkoutExerciseSetRepositoryImpl(
    private val exerciseSetDao: ExerciseSetDao,
): WorkoutExerciseSetRepository {
    override fun getWorkoutExerciseSets(workoutExerciseId: Long): Flow<Result<List<WorkoutExerciseSet>>> {
        return exerciseSetDao.getExerciseSets(workoutExerciseId)
            .map<List<ExerciseSetEntity>, Result<List<WorkoutExerciseSet>>> {
                    list -> Result.Success(list.map { it.toWorkoutExerciseSet() })
            }
            .catch { e ->
                Log.e("WorkoutExerciseSetRepositoryImpl", "getWorkoutExerciseSets", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override suspend fun addWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit> {
        val exerciseSetEntity = workoutExerciseSet.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toExerciseSetEntity()

        return try {
            exerciseSetDao.addExerciseSet(exerciseSetEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("WorkoutExerciseSetRepositoryImpl", "addWorkoutExerciseSet", e)

            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                else -> Result.Failure(AppError.UNKNOWN)
            }
        }
    }

    override suspend fun deleteWorkoutExerciseSetById(id: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit> {
        TODO("Not yet implemented")
    }

}