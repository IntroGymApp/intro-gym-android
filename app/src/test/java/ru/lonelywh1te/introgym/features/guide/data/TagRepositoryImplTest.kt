package ru.lonelywh1te.introgym.features.guide.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.data.db.dao.TagDao
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.features.guide.domain.repository.TagRepository

class TagRepositoryImplTest {
    private lateinit var tagRepository: TagRepository
    private lateinit var tagDao: TagDao

    private val testTagEntity = TagEntity(1, "test_name", TagType.OTHER)

    @BeforeEach
    fun setUp() {
        tagDao = mockk()
        tagRepository = TagRepositoryImpl(tagDao)

        every { tagDao.getTags() } returns flowOf(listOf(testTagEntity))
    }

    @Nested
    inner class GetTagsTests {
        @Test
        fun `getTags calls tagDao_getTags once`() {
            tagRepository.getTags()

            verify(exactly = 1) { tagDao.getTags() }
        }

        @Test
        fun `getTags returns flow correctly`() = runTest {
            val expected = listOf(testTagEntity.toTag())
            val actual = tagRepository.getTags().first()

            assertEquals(expected, actual)
        }
    }
}