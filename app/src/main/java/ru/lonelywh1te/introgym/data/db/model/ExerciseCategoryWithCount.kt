package ru.lonelywh1te.introgym.data.db.model

data class ExerciseCategoryWithCount(
    val id: Long,
    val name: String,
    val countOfExercises: Int,
    val imgFilename: String,
)
