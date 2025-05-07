package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.core.result.Result

interface WorkoutExerciseSetRepository {

    fun getWorkoutSets(workoutId: Long): Flow<Result<List<WorkoutExerciseSet>>>

    fun getWorkoutExerciseSets(workoutExerciseId: Long): Flow<Result<List<WorkoutExerciseSet>>>

    suspend fun addWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit>

    suspend fun deleteWorkoutExerciseSetById(id: Long): Result<Unit>

    suspend fun updateWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit>

}