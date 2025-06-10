package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import java.time.LocalDateTime
import java.util.UUID

class WorkoutExercisePlanRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutExercisePlanRepository {
    override fun getWorkoutExercisePlans(workoutId: UUID): Flow<Result<List<WorkoutExercisePlan>>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId)
            .flatMapLatest { workoutExercises ->
                val ids = workoutExercises.map { it.id }

                workoutExercisePlanDao.getWorkoutExercisePlans(ids)
                    .map<List<WorkoutExercisePlanEntity>, Result<List<WorkoutExercisePlan>>> { list ->
                        Result.Success(list.map { it.toWorkoutExercisePlan() })
                    }
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutExercisePlanById(workoutExerciseId: UUID): Flow<Result<WorkoutExercisePlan>> {
        return workoutExercisePlanDao.getWorkoutExercisePlanById(workoutExerciseId)
            .map<WorkoutExercisePlanEntity, Result<WorkoutExercisePlan>> {
                Result.Success(it.toWorkoutExercisePlan())
            }
            .asSafeSQLiteFlow()
    }

    override suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit> {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        return sqliteTryCatching {
            workoutExercisePlanDao.createWorkoutExercisePlan(workoutExercisePlanEntity)
        }
    }

    override suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit> {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        return sqliteTryCatching {
            workoutExercisePlanDao.updateWorkoutExercisePlan(workoutExercisePlanEntity)
        }
    }

    override suspend fun deleteWorkoutExercisePlan(id: UUID): Result<Unit> {
        return sqliteTryCatching {
            workoutExercisePlanDao.deleteWorkoutExercisePlan(id)
        }
    }
}