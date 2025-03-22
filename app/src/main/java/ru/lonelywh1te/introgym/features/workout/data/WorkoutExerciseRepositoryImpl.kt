package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class WorkoutExerciseRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
): WorkoutExerciseRepository {
    override fun getWorkoutExerciseItems(): Flow<List<WorkoutExerciseItem>> {
        TODO("Not yet implemented")
    }

    override fun getWorkoutExerciseById(workoutId: Long): Flow<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorkoutExercise(id: Long) {
        TODO("Not yet implemented")
    }
}