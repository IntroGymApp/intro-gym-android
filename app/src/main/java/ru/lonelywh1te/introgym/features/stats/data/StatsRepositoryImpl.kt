package ru.lonelywh1te.introgym.features.stats.data

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.model.ExerciseSetWithExerciseCategoryId
import ru.lonelywh1te.introgym.data.db.model.ExerciseSetWithWorkoutLogDate
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.model.MuscleEntry
import ru.lonelywh1te.introgym.features.stats.domain.model.WeightEntry
import ru.lonelywh1te.introgym.features.stats.domain.repository.StatsRepository

class StatsRepositoryImpl(
    private val exerciseSetDao: ExerciseSetDao,
): StatsRepository {
    override fun getTotalWeightStats(period: StatsPeriod): Flow<Result<List<WeightEntry>>> {
        return exerciseSetDao.getExerciseSetsWithWorkoutLogDateAtPeriod(period.startLocalDate, period.endLocalDate)
            .map<List<ExerciseSetWithWorkoutLogDate>, Result<List<WeightEntry>>> { list ->
                Result.Success(list.map {
                    WeightEntry(it.workoutLogDate, it.exerciseSet.weightKg ?: 0.0f)
                })
            }
            .catch { e ->
                Log.e("StatsRepositoryImpl", "getTotalWeightStats", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

    override fun getMusclesStats(period: StatsPeriod): Flow<Result<List<MuscleEntry>>> {
        return exerciseSetDao.getExerciseSetsWithExerciseCategoryIdAtPeriod(period.startLocalDate, period.endLocalDate)
            .map<List<ExerciseSetWithExerciseCategoryId>, Result<List<MuscleEntry>>> { list ->
                Result.Success(list.map {
                    MuscleEntry(it.exerciseCategoryName, it.exerciseSet.effort ?: 0)
                })
            }
            .catch { e ->
                Log.e("StatsRepositoryImpl", "getMusclesStats", e)

                val errorResult = when (e) {
                    is SQLiteException -> Result.Failure(DatabaseError.SQLITE_ERROR)
                    else -> Result.Failure(AppError.UNKNOWN)
                }

                emit(errorResult)
            }
    }

}