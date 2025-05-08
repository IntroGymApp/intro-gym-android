package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Transaction
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity

data class WorkoutExerciseWithExerciseInfo (
    @Embedded val workoutExercise: WorkoutExerciseEntity,

    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "id",
        entity = ExerciseEntity::class,
    )
    val exerciseInfo: ExerciseInfo
)

