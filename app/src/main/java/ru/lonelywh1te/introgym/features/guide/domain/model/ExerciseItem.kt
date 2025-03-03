package ru.lonelywh1te.introgym.features.guide.domain.model

data class ExerciseItem(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val imgFilename: String,
)
