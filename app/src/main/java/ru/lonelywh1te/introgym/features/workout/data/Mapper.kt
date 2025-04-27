package ru.lonelywh1te.introgym.features.workout.data

import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises
import ru.lonelywh1te.introgym.data.db.model.WorkoutExerciseWithExerciseInfo
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan

fun WorkoutEntityWithCountOfExercises.toWorkoutItem(): WorkoutItem {
    return WorkoutItem(
        workoutId = this.workoutEntity.id,
        name = this.workoutEntity.name,
        countOfExercises = this.countOfExercises,
        order = this.workoutEntity.order,
    )
}

fun WorkoutEntity.toWorkout(): Workout {
    return Workout(
        id = this.id,
        name = this.name,
        description = this.description,
        isTemplate = false,
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
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

fun WorkoutExerciseWithExerciseInfo.toWorkoutExerciseItem(): WorkoutExerciseItem {
    return WorkoutExerciseItem(
        workoutExerciseId = this.workoutExercise.id,
        name = this.exerciseInfo.name,
        imgFilename = this.exerciseInfo.imgFilename,
        order = this.workoutExercise.order,
    )
}

fun WorkoutExerciseEntity.toWorkoutExercise(): WorkoutExercise {
    return WorkoutExercise(
        id = this.id,
        workoutId = this.workoutId,
        exerciseId = this.exerciseId,
        comment = this.comment ?: "",
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutExercise.toWorkoutExerciseEntity(): WorkoutExerciseEntity {
    return WorkoutExerciseEntity(
        id = this.id,
        workoutId = this.workoutId,
        exerciseId = this.exerciseId,
        comment = this.comment,
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutExercisePlanEntity.toWorkoutExercisePlan(): WorkoutExercisePlan {
    return WorkoutExercisePlan(
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