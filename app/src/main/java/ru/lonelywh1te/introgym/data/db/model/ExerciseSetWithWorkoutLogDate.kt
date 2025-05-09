package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import java.time.LocalDate

data class ExerciseSetWithWorkoutLogDate (

    @Embedded
    val exerciseSet: ExerciseSetEntity,

    @ColumnInfo(name = "workout_log_date")
    val workoutLogDate: LocalDate,

)