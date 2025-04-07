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
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class GetExerciseListUseCaseTest {
    private lateinit var repository: ExerciseRepository
    private lateinit var getExerciseListUseCase: GetExerciseListUseCase

    private val testCategoryId = 2L
    private val testExerciseItems = listOf(
        ExerciseItem(1L, "test_name", 1L, listOf(1, 1), "img_filename"),
        ExerciseItem(1L, "test_name", 2L, listOf(1, 1), "img_filename"),
        ExerciseItem(1L, "test_name", 3L, listOf(1, 1), "img_filename"),
    )

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getExerciseListUseCase = GetExerciseListUseCase(repository)

        every { repository.getExercisesWithTags() } returns flowOf(testExerciseItems)
    }

    @Test
    fun `GetExerciseList calls repository_getExercisesWithTags once`() {
        getExerciseListUseCase(testCategoryId)

        verify(exactly = 1) { repository.getExercisesWithTags() }
    }

    @Test
    fun `GetExerciseList filter exercise items by categoryId`() = runTest {
        val expected = testExerciseItems.filter { it.categoryId == testCategoryId}
        val actual = getExerciseListUseCase(testCategoryId).first()

        assertEquals(expected, actual)
    }
}