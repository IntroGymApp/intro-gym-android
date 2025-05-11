package ru.lonelywh1te.introgym.features.workout.data

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.DatabaseError
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class WorkoutRepositoryImpl (
    private val db: MainDatabase,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutRepository {
    private val zoneOffset = ZoneOffset.UTC

    override fun getWorkoutItems(): Flow<Result<List<WorkoutItem>>> {
        return workoutDao.getWorkoutListWithCountOfExercises()
            .map<List<WorkoutEntityWithCountOfExercises>, Result<List<WorkoutItem>>> { list ->
                Result.Success(list.map { entity -> entity.toWorkoutItem() })
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkouts(): Result<List<Workout>> {
        return sqliteTryCatching {
            workoutDao.getWorkouts().map { it.toWorkout() }
        }
    }

    override fun getWorkoutById(workoutId: Long): Flow<Result<Workout>> {
        return workoutDao.getWorkoutById(workoutId)
            .filterNotNull()
            .map<WorkoutEntity, Result<Workout>> { Result.Success(it.toWorkout()) }
            .asSafeSQLiteFlow()
    }

    override suspend fun createFullWorkout(
        workout: Workout,
        exercisesWithPlans: Map<WorkoutExercise, WorkoutExercisePlan>
    ): Result<Unit> {
        return sqliteTryCatching {
            db.withTransaction {
                val countOfWorkouts = workoutDao.getCountOfWorkouts()

                val workoutEntity = workout.copy(
                    id = 0L,
                    order = countOfWorkouts,
                    createdAt = LocalDateTime.now(zoneOffset),
                    lastUpdated = LocalDateTime.now(zoneOffset),
                ).toWorkoutEntity()

                val workoutId = workoutDao.createWorkout(workoutEntity)

                exercisesWithPlans.forEach { (exercise, plan) ->
                    val exerciseEntity = exercise.copy(
                        id = 0L,
                        workoutId = workoutId,
                        createdAt = LocalDateTime.now(zoneOffset),
                        lastUpdated = LocalDateTime.now(zoneOffset),
                    ).toWorkoutExerciseEntity()

                    val workoutExerciseId = workoutExerciseDao.addWorkoutExercise(exerciseEntity)

                    val planEntity = plan.copy(
                        id = 0L,
                        createdAt = LocalDateTime.now(zoneOffset),
                        lastUpdated = LocalDateTime.now(zoneOffset),
                        workoutExerciseId = workoutExerciseId
                    ).toWorkoutExercisePlanEntity()

                    workoutExercisePlanDao.createWorkoutExercisePlan(planEntity)
                }
            }
        }
    }

    override suspend fun updateWorkout(workout: Workout): Result<Unit> {
        val workoutEntity = workout.copy(
            lastUpdated = LocalDateTime.now(zoneOffset)
        ).toWorkoutEntity()

        return sqliteTryCatching { workoutDao.updateWorkout(workoutEntity) }
    }

    override suspend fun deleteWorkoutWithReorder(id: Long): Result<Unit> {
        return sqliteTryCatching {
            db.withTransaction {
                val workoutToDelete = workoutDao.getWorkoutById(id).first()
                val workoutsToReorder = workoutDao.getWorkoutsWithOrderGreaterThan(workoutToDelete.order)

                workoutDao.deleteWorkout(workoutToDelete.id)

                workoutsToReorder.forEach {
                    val updatedWorkout = it.copy(order = it.order - 1)
                    workoutDao.updateWorkout(updatedWorkout)
                }
            }
        }
    }

    override suspend fun getCountOfWorkouts(): Result<Int> {
        return sqliteTryCatching { workoutDao.getCountOfWorkouts() }
    }
}