package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity

data class WorkoutExerciseWithExerciseInfo (
    @Embedded val workoutExercise: WorkoutExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workout_exercise_id",
        entity = ExerciseEntity::class,
    )
    val exerciseInfo: ExerciseInfo
)

