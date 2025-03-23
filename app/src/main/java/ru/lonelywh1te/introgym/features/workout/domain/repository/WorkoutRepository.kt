package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem

interface WorkoutRepository {

    fun getWorkoutItems(): Flow<List<WorkoutItem>>

    fun getWorkoutById(workoutId: Long): Flow<Workout>

    suspend fun createWorkout(workout: Workout): Long

    suspend fun updateWorkout(workout: Workout): Long

    suspend fun deleteWorkout(id: Long)

}