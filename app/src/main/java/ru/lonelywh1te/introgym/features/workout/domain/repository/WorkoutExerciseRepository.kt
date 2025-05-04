package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

interface WorkoutExerciseRepository {

    fun getWorkoutExerciseItems(workoutId: Long): Flow<Result<List<WorkoutExerciseItem.Default>>>

    fun getWorkoutExerciseItemsWithProgress(workoutId: Long): Flow<Result<List<WorkoutExerciseItem.WithProgress>>>

    fun getWorkoutExercisesByWorkoutId(workoutId: Long): Flow<Result<List<WorkoutExercise>>>

    fun getWorkoutExerciseById(id: Long): Flow<Result<WorkoutExercise>>

    suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Result<Long>

    suspend fun addWorkoutExerciseWithEmptyPlan(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun deleteWorkoutExercise(id: Long): Result<Unit>

    suspend fun deleteWorkoutExerciseWithReorder(id: Long): Result<Unit>

}