package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem

interface WorkoutRepository {

    fun getWorkoutItems(): Flow<Result<List<WorkoutItem>>>

    fun getWorkouts(): Result<List<Workout>>

    fun getWorkoutById(workoutId: Long): Flow<Result<Workout>>

    suspend fun createWorkout(workout: Workout): Result<Long>

    suspend fun updateWorkout(workout: Workout): Result<Unit>

    suspend fun deleteWorkout(id: Long): Result<Unit>

    suspend fun getCountOfWorkouts(): Result<Int>

}