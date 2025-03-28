package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

interface WorkoutExercisePlanRepository {

    fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<WorkoutExercisePlan>

    suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Long

    suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan)

    suspend fun deleteWorkoutExercisePlan(id: Long)

}