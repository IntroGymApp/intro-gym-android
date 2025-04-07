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
import ru.lonelywh1te.introgym.data.db.dao.ExerciseCategoryDao
import ru.lonelywh1te.introgym.data.db.model.ExerciseCategoryWithCount
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository

class ExerciseCategoryRepositoryImplTest {
    private lateinit var exerciseCategoryRepository: ExerciseCategoryRepository
    private lateinit var exerciseCategoryDao: ExerciseCategoryDao

    private val testExerciseCategoryWithCount = ExerciseCategoryWithCount(1, "test_name", 1, "img_filename")

    @BeforeEach
    fun setUp() {
        exerciseCategoryDao = mockk()
        exerciseCategoryRepository = ExerciseCategoryRepositoryImpl(exerciseCategoryDao)

        every { exerciseCategoryDao.getCategoriesWithExerciseCount() } returns flowOf(listOf(testExerciseCategoryWithCount))
    }

    @Nested
    inner class GetCategoriesTests() {
        @Test
        fun `getCategories calls exerciseCategoryDao_getCategoriesWithExerciseCount once`() {
            exerciseCategoryRepository.getCategories()

            verify(exactly = 1) { exerciseCategoryDao.getCategoriesWithExerciseCount() }
        }

        @Test
        fun `getCategories returns flow correctly`() = runTest {
            val expected = listOf(testExerciseCategoryWithCount.toExerciseCategoryItem())
            val actual = exerciseCategoryRepository.getCategories().first()

            assertEquals(expected, actual)
        }
    }

}