package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class WorkoutRepositoryImpl (
    private val workoutDao: WorkoutDao
): WorkoutRepository {
    override fun getWorkoutItems(): Flow<List<WorkoutItem>> {
        TODO("Not yet implemented")
    }

    override fun getWorkoutById(id: Int): Flow<WorkoutItem> {
        TODO("Not yet implemented")
    }

    override fun createWorkout(workout: Workout) {
        TODO("Not yet implemented")
    }

    override fun updateWorkout(workout: Workout) {
        TODO("Not yet implemented")
    }

    override fun deleteWorkout(id: Int) {
        TODO("Not yet implemented")
    }

}