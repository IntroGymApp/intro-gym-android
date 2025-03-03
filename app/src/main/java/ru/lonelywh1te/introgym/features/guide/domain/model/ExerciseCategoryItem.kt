package ru.lonelywh1te.introgym.features.guide.domain.model

data class ExerciseCategoryItem(
    val id: Long,
    val name: String,
    val countOfExercises: Int,
    val imgFilename: String,
)