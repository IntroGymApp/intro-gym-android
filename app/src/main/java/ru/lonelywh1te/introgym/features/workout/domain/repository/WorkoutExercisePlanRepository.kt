package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

interface WorkoutExercisePlanRepository {

    fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<Result<WorkoutExercisePlan>>

    suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Long>

    suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit>

    suspend fun deleteWorkoutExercisePlan(id: Long): Result<Unit>

}