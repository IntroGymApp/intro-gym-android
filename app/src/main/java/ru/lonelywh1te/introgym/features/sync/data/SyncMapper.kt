package ru.lonelywh1te.introgym.features.sync.data

import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncExerciseSetDto
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncWorkoutDto
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncWorkoutExerciseDto
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncWorkoutExercisePlanDto
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncWorkoutLogDto

fun WorkoutEntity.toSyncWorkoutDto(): SyncWorkoutDto {
    return SyncWorkoutDto(
        id = id.toString(),
        name = name,
        description = description,
        isTemplate = isTemplate,
        order = order,
        createdAt = createdAt.toString(),
        lastUpdated = lastUpdated.toString(),
    )
}

fun WorkoutLogEntity.toSyncWorkoutLogDto(): SyncWorkoutLogDto {
    return SyncWorkoutLogDto(
        id = id.toString(),
        workoutId = workoutId.toString(),
        date = date.toString(),
        startDateTime = startDateTime.toString(),
        endDateTime = endDateTime.toString(),
        order = order,
        createdAt = createdAt.toString(),
        lastUpdated = lastUpdated.toString(),
    )
}

fun WorkoutExerciseEntity.toSyncWorkoutExerciseDto(): SyncWorkoutExerciseDto {
    return SyncWorkoutExerciseDto(
        id = id.toString(),
        workoutId = workoutId.toString(),
        exerciseId = exerciseId,
        order = order,
        createdAt = createdAt.toString(),
        lastUpdated = lastUpdated.toString(),
    )
}

fun WorkoutExercisePlanEntity.toSyncWorkoutExercisePlanDto(): SyncWorkoutExercisePlanDto {
    return SyncWorkoutExercisePlanDto(
        id = id.toString(),
        workoutExerciseId = workoutExerciseId.toString(),
        weightKg = weightKg,
        sets = sets,
        reps = reps,
        timeInSec = timeInSec,
        distanceInMeters = distanceInMeters,
        createdAt = createdAt.toString(),
        lastUpdated = lastUpdated.toString(),
    )
}

fun ExerciseSetEntity.toSyncExerciseSetDto(): SyncExerciseSetDto {
    return SyncExerciseSetDto(
        id = id.toString(),
        workoutExerciseId = workoutExerciseId.toString(),
        reps = reps,
        weightKg = weightKg,
        timeInSec = timeInSec,
        distanceInMeters = distanceInMeters,
        effort = effort,
        createdAt = createdAt.toString(),
        lastUpdated = lastUpdated.toString(),
    )
}