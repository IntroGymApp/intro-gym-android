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
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository

class GetExerciseCategoriesUseCaseTest {
    private lateinit var repository: ExerciseCategoryRepository
    private lateinit var getExerciseCategoriesUseCase: GetExerciseCategoriesUseCase

    private val testExerciseCategoryItemList = listOf(
        ExerciseCategoryItem(1, "test_name", 1, "img_filename"),
        ExerciseCategoryItem(2, "test_name", 2, "img_filename")

    )

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getExerciseCategoriesUseCase = GetExerciseCategoriesUseCase(repository)

        every { repository.getCategories() } returns flowOf(testExerciseCategoryItemList)
    }

    @Test
    fun `GetExerciseCategories calls repository_getCategories once`() {
        getExerciseCategoriesUseCase()

        verify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `GetExerciseCategories returns flow correctly`() = runTest {
        val expected = testExerciseCategoryItemList
        val actual = getExerciseCategoriesUseCase().first()

        assertEquals(expected, actual)
    }
}