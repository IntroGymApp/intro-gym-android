package ru.lonelywh1te.introgym.features.workout.data

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutInfo
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class WorkoutRepositoryImpl (
    private val db: RoomDatabase,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
    private val exerciseDao: ExerciseDao,
): WorkoutRepository {
    override fun getWorkoutItems(): Flow<List<WorkoutItem>> {
        return workoutDao.getWorkoutsWithExercises().map { list ->
            list.map { it.toWorkoutItem() }
        }
    }

    override fun getWorkoutInfoById(workoutId: Long): Flow<WorkoutInfo> {
        return workoutDao.getWorkoutWithExercisesById(workoutId).map { workoutWithExercises ->
            val workoutExercises = workoutWithExercises.workoutExercises

            val exerciseEntityShortMap = exerciseDao
                .getExercisesShortByIds(workoutWithExercises.workoutExercises.map { it.id })
                .associateBy { it.id }

            val workoutExerciseItems = workoutExercises.mapNotNull { workoutExercise ->
                exerciseEntityShortMap[workoutExercise.exerciseId]?.let { exerciseShortInfo ->
                    WorkoutExerciseItem(
                        workoutExerciseId = workoutExercise.id,
                        name = exerciseShortInfo.name,
                        imgFilename = exerciseShortInfo.imgFilename,
                        order = workoutExercise.order
                    )
                }
            }

            WorkoutInfo(
                name = workoutWithExercises.workoutEntity.name,
                description = workoutWithExercises.workoutEntity.description,
                workoutExerciseItems = workoutExerciseItems
            )
        }
    }

    override suspend fun createWorkout(workout: Workout) {
        db.withTransaction {
            workoutDao.createWorkout(workout.toWorkoutEntity())

            workout.workoutExercises.forEachIndexed { index, workoutExercise ->
                workoutExerciseDao.addWorkoutExercise(
                    WorkoutExerciseEntity(
                        id = workoutExercise.id,
                        workoutId = workout.id,
                        exerciseId = workoutExercise.exerciseId,
                        comment = workoutExercise.comment,
                        order = index,
                        createdAt = workoutExercise.createdAt,
                        lastUpdated = workoutExercise.lastUpdated,
                    )
                )

                workoutExercisePlanDao.createWorkoutExercisePlan(workoutExercise.plan.toWorkoutExercisePlanEntity())
            }
        }
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout.toWorkoutEntity())
    }

    override suspend fun deleteWorkout(id: Long) {
        workoutDao.deleteWorkout(id)
    }

}