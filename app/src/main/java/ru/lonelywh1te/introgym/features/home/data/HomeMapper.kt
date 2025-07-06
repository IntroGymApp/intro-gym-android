package ru.lonelywh1te.introgym.features.home.data

import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.workout.data.toWorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState
import java.util.UUID

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