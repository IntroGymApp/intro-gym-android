package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

interface WorkoutExerciseRepository {

    fun getWorkoutExerciseItems(): Flow<List<WorkoutExerciseItem>>

    fun getWorkoutExerciseById(workoutId: Int): Flow<WorkoutExercise>

    fun addWorkoutExercise(workoutExercise: WorkoutExercise)

    fun updateWorkoutExercise(workoutExercise: WorkoutExercise)

    fun deleteWorkoutExercise(id: Int)

}