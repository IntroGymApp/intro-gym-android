package ru.lonelywh1te.introgym.features.guide.domain.model

import ru.lonelywh1te.introgym.data.db.TagType

data class Tag (
    val id: Int,
    val name: String,
    val type: TagType,
)