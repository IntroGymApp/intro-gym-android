package ru.lonelywh1te.introgym.data.db.model

// TODO: переделать с embedded

data class ExerciseCategoryWithCount(
    val id: Long,
    val name: String,
    val countOfExercises: Int,
    val imgFilename: String,
)
