package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import java.time.LocalDate

data class ExerciseSetWithExerciseCategoryId(

    @Embedded
    val exerciseSet: ExerciseSetEntity,

    @ColumnInfo(name = "exercise_category_name")
    val exerciseCategoryName: String,
)
