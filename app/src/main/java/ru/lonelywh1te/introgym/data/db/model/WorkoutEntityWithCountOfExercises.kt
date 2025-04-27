package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity

data class WorkoutEntityWithCountOfExercises (

    @Embedded
    val workoutEntity: WorkoutEntity,

    @ColumnInfo(name = "count_of_exercises")
    val countOfExercises: Int,

)