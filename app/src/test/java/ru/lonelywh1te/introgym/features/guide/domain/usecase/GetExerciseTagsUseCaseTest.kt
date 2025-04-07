package ru.lonelywh1te.introgym.features.guide.domain.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag
import ru.lonelywh1te.introgym.features.guide.domain.repository.TagRepository

class GetExerciseTagsUseCaseTest {

    private lateinit var repository: TagRepository
    private lateinit var getExerciseTagsUseCase: GetExerciseTagsUseCase

    private val testTags = List(3) { index -> Tag(index, "test_name", TagType.OTHER) }

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getExerciseTagsUseCase = GetExerciseTagsUseCase(repository)

        every { repository.getTags() } returns flowOf(testTags)
    }

    @Test
    fun `GetExerciseTags calls repository_getTags once`() {
        getExerciseTagsUseCase()

        verify(exactly = 1) { repository.getTags() }
    }

    @Test
    fun `GetExerciseTags returns flow correctly`() = runTest {
        val expected = testTags
        val actual = getExerciseTagsUseCase().first()

        assertEquals(expected, actual)
    }

}