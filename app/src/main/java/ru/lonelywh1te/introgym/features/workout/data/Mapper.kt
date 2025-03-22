package ru.lonelywh1te.introgym.features.workout.data

import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithExercises
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

fun WorkoutEntityWithExercises.toWorkoutItem(): WorkoutItem{
    return WorkoutItem(
        workoutId = this.workoutEntity.id,
        name = this.workoutEntity.name,
        countOfExercises = this.workoutExercises.size,
        order = this.workoutEntity.order,
    )
}

fun Workout.toWorkoutEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        isTemplate = true,
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutExercisePlan.toWorkoutExercisePlanEntity(): WorkoutExercisePlanEntity {
    return WorkoutExercisePlanEntity(
        id = this.id,
        workoutExerciseId = this.workoutExerciseId,
        sets = this.sets,
        reps = this.reps,
        weightKg = this.weightKg,
        timeInSec = this.timeInSec,
        distanceInMeters = this.distanceInMeters,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}