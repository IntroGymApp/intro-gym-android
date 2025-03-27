package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.time.LocalDateTime

class WorkoutExerciseRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
): WorkoutExerciseRepository {

    override fun getWorkoutExerciseItems(workoutId: Long): Flow<List<WorkoutExerciseItem>> {
        return workoutExerciseDao.getWorkoutExercisesWithExerciseInfo(workoutId).map { list ->
            list.map { it.toWorkoutExerciseItem() }
        }
    }

    override fun getWorkoutExercisesById(workoutId: Long): Flow<List<WorkoutExercise>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId).map { list ->
            list.map { it.toWorkoutExercise() }
        }
    }

    override suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Long {
        val workoutExerciseEntity = workoutExercise.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        return workoutExerciseDao.addWorkoutExercise(workoutExerciseEntity)
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        val workoutExerciseEntity = workoutExercise.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExerciseEntity()

        workoutExerciseDao.updateWorkoutExercise(workoutExerciseEntity)
    }

    override suspend fun deleteWorkoutExercise(id: Long) {
        return workoutExerciseDao.deleteWorkoutExercise(id)
    }
}