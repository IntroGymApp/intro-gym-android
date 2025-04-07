package ru.lonelywh1te.introgym.features.guide.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseEntityWithTags
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class ExerciseRepositoryImplTest {
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var exerciseDao: ExerciseDao

    private val testId = 1L
    private val testExerciseEntity = ExerciseEntity(1L, 1L, "test_name", "test_desc", listOf(), listOf(), "img_filename", "anim_filename")
    private val testTagEntity = TagEntity(1, "test_name", TagType.OTHER)
    private val testExerciseEntityWithTags = ExerciseEntityWithTags(testExerciseEntity, listOf(testTagEntity, testTagEntity))

    @BeforeEach
    fun setUp() {
        exerciseDao = mockk()
        exerciseRepository = ExerciseRepositoryImpl(exerciseDao)

        every { exerciseDao.getExerciseById(any()) } returns flowOf(testExerciseEntity)
        every { exerciseDao.getExercisesWithTags() } returns flowOf(listOf(testExerciseEntityWithTags))
    }

    @Nested
    inner class GetExerciseByIdTests {
        @Test
        fun `getExerciseById calls exerciseDao_getExerciseById once`() {
            exerciseRepository.getExerciseById(testId)

            verify(exactly = 1) { exerciseDao.getExerciseById(testId) }
        }

        @Test
        fun `getExerciseById returns flow correctly`() = runTest {
            val expected = testExerciseEntity.toExercise()
            val actual = exerciseRepository.getExerciseById(testId).first()

            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class GetExercisesWithTagsTests {
        @Test
        fun `getExercisesWithTags calls exerciseDao_getExercisesWithTags once`() {
            exerciseRepository.getExercisesWithTags()

            verify(exactly = 1) { exerciseDao.getExercisesWithTags() }
        }

        @Test
        fun `getExercisesWithTags returns flow correctly`() = runTest {
            val expected = listOf(testExerciseEntityWithTags.toExerciseItem())
            val actual = exerciseRepository.getExercisesWithTags().first()

            assertEquals(expected, actual)
        }
    }


}