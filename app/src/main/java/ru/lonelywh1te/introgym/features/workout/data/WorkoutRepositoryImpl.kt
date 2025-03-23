package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository
import java.time.LocalDateTime

class WorkoutRepositoryImpl (
    private val workoutDao: WorkoutDao,
): WorkoutRepository {

    override fun getWorkoutItems(): Flow<List<WorkoutItem>> {
        return workoutDao.getWorkoutWithCountOfExercises().map { list ->
            list.map { workoutEntityWithCountOfExercises ->
                val countOfExercises = workoutEntityWithCountOfExercises.countOfExercises
                workoutEntityWithCountOfExercises.workoutEntity.toWorkoutItem(countOfExercises)
            }
        }
    }

    override fun getWorkoutById(workoutId: Long): Flow<Workout> {
        return workoutDao.getWorkoutById(workoutId).map { it.toWorkout() }
    }

    override suspend fun createWorkout(workout: Workout): Long {
        val workoutEntity = workout.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now()
        ).toWorkoutEntity()

        return workoutDao.createWorkout(workoutEntity)
    }

    override suspend fun updateWorkout(workout: Workout): Long {
        val workoutEntity = workout.copy(
            lastUpdated = LocalDateTime.now()
        ).toWorkoutEntity()

        return workoutDao.updateWorkout(workoutEntity)
    }

    override suspend fun deleteWorkout(id: Long) {
        workoutDao.deleteWorkout(id)
    }

}