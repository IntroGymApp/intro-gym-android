package ru.lonelywh1te.introgym.features.workout.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutExerciseWithExerciseInfo
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.time.LocalDateTime
import java.util.UUID

class WorkoutExerciseRepositoryImpl(
    private val db: MainDatabase,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
    private val exerciseSetDao: ExerciseSetDao,
): WorkoutExerciseRepository {

    override fun getWorkoutExerciseItems(workoutId: UUID): Flow<Result<List<WorkoutExerciseItem.Default>>> {
        return workoutExerciseDao.getWorkoutExercisesWithExerciseInfo(workoutId)
            .map<List<WorkoutExerciseWithExerciseInfo>, Result<List<WorkoutExerciseItem.Default>>> {
                list -> Result.Success(list.map { it.toWorkoutExerciseItemDefault() })
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutExerciseItemsWithProgress(workoutId: UUID): Flow<Result<List<WorkoutExerciseItem.WithProgress>>> {
        return workoutExerciseDao.getWorkoutExercisesWithExerciseInfo(workoutId)
            .map<List<WorkoutExerciseWithExerciseInfo>, Result<List<WorkoutExerciseItem.WithProgress>>> { list ->
                Result.Success(list.map {
                    val plan = workoutExercisePlanDao.getWorkoutExercisePlanById(it.workoutExercise.id).first()
                    val sets = exerciseSetDao.getExerciseSets(it.workoutExercise.id).first()

                    it.toWorkoutExerciseItemWithProgress(sets, plan)
                })
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutExercisesByWorkoutId(workoutId: UUID): Flow<Result<List<WorkoutExercise>>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId)
            .map<List<WorkoutExerciseEntity>, Result<List<WorkoutExercise>>> {
                list -> Result.Success(list.map { it.toWorkoutExercise() })
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutExerciseById(id: UUID): Flow<Result<WorkoutExercise>> {
        return workoutExerciseDao.getWorkoutExerciseById(id)
            .map<WorkoutExerciseEntity, Result<WorkoutExercise>> {
                Result.Success(it.toWorkoutExercise())
            }
            .asSafeSQLiteFlow()
    }

    override suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit> {
        val workoutExerciseEntity = workoutExercise.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return sqliteTryCatching { workoutExerciseDao.addWorkoutExercise(workoutExerciseEntity) }
    }

    override suspend fun addWorkoutExerciseWithEmptyPlan(workoutExercise: WorkoutExercise): Result<Unit> {
        val newWorkoutExerciseId = UUID.randomUUID()
        val workoutExerciseEntity = workoutExercise.copy(
            id = newWorkoutExerciseId,
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return sqliteTryCatching {
            db.withTransaction {
                workoutExerciseDao.addWorkoutExercise(workoutExerciseEntity)
                val emptyWorkoutExercisePlanEntity = WorkoutExercisePlan(
                    workoutExerciseId = newWorkoutExerciseId
                ).toWorkoutExercisePlanEntity()

                workoutExercisePlanDao.createWorkoutExercisePlan(emptyWorkoutExercisePlanEntity)
            }
        }
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit> {
        val workoutExerciseEntity = workoutExercise.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return sqliteTryCatching {
            workoutExerciseDao.updateWorkoutExercise(workoutExerciseEntity)
        }
    }

    override suspend fun deleteWorkoutExercise(id: UUID): Result<Unit> {
        return sqliteTryCatching {
            workoutExerciseDao.deleteWorkoutExercise(id)
        }
    }

    override suspend fun deleteWorkoutExerciseWithReorder(id: UUID): Result<Unit> {
        return sqliteTryCatching {
            db.withTransaction {
                val workoutToDelete = workoutExerciseDao.getWorkoutExerciseById(id).first()
                val workoutsToReorder = workoutExerciseDao.getWorkoutExercisesWithOrderGreaterThan(workoutToDelete.order)

                workoutExerciseDao.deleteWorkoutExercise(workoutToDelete.id)

                workoutsToReorder.forEach {
                    val updatedWorkout = it.copy(order = it.order - 1)
                    workoutExerciseDao.updateWorkoutExercise(updatedWorkout)
                }
            }
        }
    }
}