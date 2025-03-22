package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity

data class WorkoutEntityWithExercises (

    @Embedded
    val workoutEntity: WorkoutEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workout_id",
        entity = WorkoutExerciseEntity::class
    )
    val workoutExercises: List<WorkoutExerciseEntity>,
)