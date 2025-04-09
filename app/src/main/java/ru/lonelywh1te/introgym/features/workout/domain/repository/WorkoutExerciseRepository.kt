package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.core.result.Result

interface WorkoutExerciseRepository {

    fun getWorkoutExerciseItems(workoutId: Long): Flow<Result<List<WorkoutExerciseItem>>>

    fun getWorkoutExercisesById(workoutId: Long): Flow<Result<List<WorkoutExercise>>>

    suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Result<Long>

    suspend fun addWorkoutExerciseWithEmptyPlan(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise): Result<Unit>

    suspend fun deleteWorkoutExercise(id: Long): Result<Unit>

    suspend fun deleteWorkoutExerciseWithReorder(id: Long): Result<Unit>

}