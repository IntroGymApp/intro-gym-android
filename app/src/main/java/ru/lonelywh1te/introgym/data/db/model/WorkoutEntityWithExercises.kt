package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout

data class WorkoutEntityWithExercises (

    @Embedded
    val workout: Workout,

    @Relation(
        parentColumn = "id",
        entityColumn = "workout_id"
    )
    val exercises: List<WorkoutExerciseEntity>
)