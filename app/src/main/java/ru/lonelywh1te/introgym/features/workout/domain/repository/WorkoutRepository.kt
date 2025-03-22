package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutInfo
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem

interface WorkoutRepository {

    fun getWorkoutItems(): Flow<List<WorkoutItem>>

    fun getWorkoutInfoById(workoutId: Long): Flow<WorkoutInfo>

    suspend fun createWorkout(workout: Workout)

    suspend fun updateWorkout(workout: Workout)

    suspend fun deleteWorkout(id: Long)

}