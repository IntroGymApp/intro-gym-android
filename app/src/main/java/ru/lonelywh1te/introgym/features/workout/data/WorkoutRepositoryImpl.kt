package ru.lonelywh1te.introgym.features.workout.data

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class WorkoutRepositoryImpl (
    private val workoutDao: WorkoutDao,
): WorkoutRepository {
    private val zoneOffset = ZoneOffset.UTC

    override fun getWorkoutItems(): Flow<List<WorkoutItem>> {
        return workoutDao.getWorkoutWithCountOfExercises().map { list ->
            list.map { workoutEntityWithCountOfExercises ->
                workoutEntityWithCountOfExercises.toWorkoutItem()
            }
        }
    }

    override fun getWorkoutById(workoutId: Long): Flow<Workout> {
        return workoutDao.getWorkoutById(workoutId).map { it.toWorkout() }
    }

    override suspend fun createWorkout(workout: Workout): Long {
        val workoutEntity = workout.copy(
            createdAt = LocalDateTime.now(zoneOffset),
            lastUpdated = LocalDateTime.now(zoneOffset)
        ).toWorkoutEntity()

        return workoutDao.createWorkout(workoutEntity)
    }

    override suspend fun updateWorkout(workout: Workout) {
        val workoutEntity = workout.copy(
            lastUpdated = LocalDateTime.now(zoneOffset)
        ).toWorkoutEntity()

        workoutDao.updateWorkout(workoutEntity)
    }

    override suspend fun deleteWorkout(id: Long) {
        workoutDao.deleteWorkout(id)
    }
}