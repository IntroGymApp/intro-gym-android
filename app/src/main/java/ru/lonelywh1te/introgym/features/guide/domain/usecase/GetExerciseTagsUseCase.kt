package ru.lonelywh1te.introgym.features.guide.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag
import ru.lonelywh1te.introgym.features.guide.domain.repository.TagRepository

class GetExerciseTagsUseCase(private val repository: TagRepository) {
    operator fun invoke(): Flow<List<Tag>> {
        return repository.getTags()
    }
}