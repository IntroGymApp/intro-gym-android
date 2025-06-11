package ru.lonelywh1te.introgym.features.home.data

import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState
import java.util.UUID

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

fun WorkoutLogEntity.toWorkoutLogItem(workoutEntity: WorkoutEntity, countOfExercises: Int): WorkoutLogItem {
    return WorkoutLogItem(
        workoutLogId = this.id,
        workoutId = workoutEntity.id,
        workoutName = workoutEntity.name,
        workoutDescription = workoutEntity.description,
        countOfExercises = countOfExercises,
        order = this.order,
        state = WorkoutLogState.get(this.toWorkoutLog()),
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