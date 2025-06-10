package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import java.util.UUID

interface WorkoutExercisePlanRepository {

    fun getWorkoutExercisePlans(workoutId: UUID): Flow<Result<List<WorkoutExercisePlan>>>

    fun getWorkoutExercisePlanById(workoutExerciseId: UUID): Flow<Result<WorkoutExercisePlan>>

    suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit>

    suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Result<Unit>

    suspend fun deleteWorkoutExercisePlan(id: Long): Result<Unit>
    suspend fun deleteWorkoutExercisePlan(id: UUID): Result<Unit>

}