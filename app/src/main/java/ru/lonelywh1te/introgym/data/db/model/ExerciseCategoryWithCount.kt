package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.lonelywh1te.introgym.data.db.entity.ExerciseCategoryEntity

data class ExerciseCategoryWithCount(

    @Embedded
    val exerciseCategoryEntity: ExerciseCategoryEntity,

    @ColumnInfo("count_of_exercises")
    val countOfExercises: Int,

)
