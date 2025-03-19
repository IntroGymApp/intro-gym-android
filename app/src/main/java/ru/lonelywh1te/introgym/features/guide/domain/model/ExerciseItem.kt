package ru.lonelywh1te.introgym.features.guide.domain.model

data class ExerciseItem(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val tagsIds: List<Int>,
    val imgFilename: String,
)
