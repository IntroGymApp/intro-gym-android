package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import java.util.UUID

interface WorkoutExerciseRepository {

    fun getWorkoutExerciseItems(workoutId: UUID): Flow<Result<List<WorkoutExerciseItem.Default>>>

    fun getWorkoutExerciseItemsWithProgress(workoutId: UUID): Flow<Result<List<WorkoutExerciseItem.WithProgress>>>

    fun getWorkoutExercisesByWorkoutId(workoutId: UUID): Flow<Result<List<WorkoutExercise>>>

    fun getWorkoutExerciseById(id: UUID): Flow<Result<WorkoutExercise>>

    suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun addWorkoutExerciseWithEmptyPlan(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun deleteWorkoutExercise(id: UUID): Result<Unit>

    suspend fun deleteWorkoutExerciseWithReorder(id: Long): Result<Unit>

}