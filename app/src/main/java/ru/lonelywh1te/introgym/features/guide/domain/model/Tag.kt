package ru.lonelywh1te.introgym.features.guide.domain.model

import ru.lonelywh1te.introgym.data.db.model.TagType

data class Tag (
    val id: Int,
    val name: String,
    val type: TagType,
)