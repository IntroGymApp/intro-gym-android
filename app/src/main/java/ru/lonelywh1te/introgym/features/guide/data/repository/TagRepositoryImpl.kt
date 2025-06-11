package ru.lonelywh1te.introgym.features.guide.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.TagDao
import ru.lonelywh1te.introgym.features.guide.data.toTag
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag
import ru.lonelywh1te.introgym.features.guide.domain.repository.TagRepository

class TagRepositoryImpl(
    private val tagDao: TagDao,
): TagRepository {

    override fun getTags(): Flow<List<Tag>> {
        return tagDao.getTags().map { tagEntities ->
            tagEntities.map { it.toTag() }
        }
    }

}