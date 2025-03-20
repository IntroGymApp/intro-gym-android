package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

interface WorkoutExercisePlanRepository {

    fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<List<WorkoutExercisePlan>>

    fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan)

    fun updateWorkoutExercise(workoutExercisePlan: WorkoutExercisePlan)

    fun deleteWorkoutExercise(id: Int)

}