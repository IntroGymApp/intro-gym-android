package ru.lonelywh1te.introgym.features.workout.data

import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises
import ru.lonelywh1te.introgym.data.db.model.WorkoutExerciseWithExerciseInfo
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import java.util.UUID

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
        isTemplate = this.isTemplate,
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
        isTemplate = this.isTemplate,
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutExerciseWithExerciseInfo.toWorkoutExerciseItemDefault(): WorkoutExerciseItem.Default {
    return WorkoutExerciseItem.Default(
        workoutExerciseId = this.workoutExercise.id,
        name = this.exerciseInfo.name,
        imgFilename = this.exerciseInfo.imgFilename,
        order = this.workoutExercise.order,
    )
}

fun WorkoutExerciseWithExerciseInfo.toWorkoutExerciseItemWithProgress(
    sets: List<ExerciseSetEntity>,
    plan: WorkoutExercisePlanEntity,
): WorkoutExerciseItem.WithProgress {
    return WorkoutExerciseItem.WithProgress(
        workoutExerciseId = this.workoutExercise.id,
        name = this.exerciseInfo.name,
        imgFilename = this.exerciseInfo.imgFilename,
        plannedSets = plan.sets ?: 0,
        completedSets = sets.size,
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

fun ExerciseSetEntity.toWorkoutExerciseSet(): WorkoutExerciseSet {
    return WorkoutExerciseSet(
        id = this.id,
        reps = this.reps,
        workoutExerciseId = this.workoutExerciseId,
        weightKg = this.weightKg,
        distanceInMeters = this.distanceInMeters,
        timeInSeconds = this.timeInSec,
        effort = Effort.fromPercent(this.effort),
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutExerciseSet.toExerciseSetEntity(): ExerciseSetEntity {
    return ExerciseSetEntity(
        id = this.id,
        workoutExerciseId = this.workoutExerciseId,
        reps = this.reps,
        weightKg = this.weightKg,
        distanceInMeters = this.distanceInMeters,
        timeInSec = this.timeInSeconds,
        effort = this.effort?.toPercent(),
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
    )
}

fun WorkoutLog.toWorkoutLogEntity(): WorkoutLogEntity {
    return WorkoutLogEntity(
        id = this.id ?: UUID.randomUUID(),
        workoutId = this.workoutId,
        date = this.date,
        startDateTime = this.startDateTime,
        endDateTime = this.endDateTime,
        order = this.order,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdatedAt,
        isSynchronized = this.isSynchronized,
    )
}

fun WorkoutLogEntity.toWorkoutLog(): WorkoutLog {
    return WorkoutLog(
        id = this.id,
        workoutId = this.workoutId,
        date = this.date,
        startDateTime = this.startDateTime,
        endDateTime = this.endDateTime,
        order = this.order,
    )
}