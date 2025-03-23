package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity

data class WorkoutEntityWithCountOfExercises (

    @Embedded
    val workoutEntity: WorkoutEntity,

    val countOfExercises: Int,

)