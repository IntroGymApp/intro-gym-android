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

    override fun getWorkoutExerciseById(id: Int): Flow<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override fun addWorkoutExercise(workoutExercise: WorkoutExercise) {
        TODO("Not yet implemented")
    }

    override fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        TODO("Not yet implemented")
    }

    override fun deleteWorkoutExercise(id: Int) {
        TODO("Not yet implemented")
    }
}