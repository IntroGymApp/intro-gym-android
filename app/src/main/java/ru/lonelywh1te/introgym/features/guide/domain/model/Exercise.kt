package ru.lonelywh1te.introgym.features.guide.domain.model

data class Exercise(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val description: String,
    val steps: List<String>,
    val tips: List<String>,
    val animFilename: String,
)
