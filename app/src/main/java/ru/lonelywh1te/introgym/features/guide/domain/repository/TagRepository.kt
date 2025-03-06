package ru.lonelywh1te.introgym.features.guide.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

interface TagRepository {
    fun getTags(): Flow<List<Tag>>
}