package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem

interface WorkoutRepository {

    fun getWorkoutItems(): Flow<List<WorkoutItem>>

    fun getWorkoutById(id: Int): Flow<WorkoutItem>

    fun createWorkout(workout: Workout)

    fun updateWorkout(workout: Workout)

    fun deleteWorkout(id: Int)

}