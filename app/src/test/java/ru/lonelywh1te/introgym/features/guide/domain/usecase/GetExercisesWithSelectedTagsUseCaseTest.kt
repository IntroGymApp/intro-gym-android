package ru.lonelywh1te.introgym.features.guide.domain.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class GetExercisesWithSelectedTagsUseCaseTest {
    private lateinit var repository: ExerciseRepository
    private lateinit var getExercisesWithSelectedTagsUseCase: GetExercisesWithSelectedTagsUseCase

    private val testExerciseItems = listOf(
        ExerciseItem(1L, "test_name", 1L, listOf(1, 3), "img_filename"),
        ExerciseItem(1L, "test_name", 1L, listOf(1, 2, 3), "img_filename"),
        ExerciseItem(1L, "test_name", 1L, listOf(4, 5), "img_filename")
    )
    private val testTagsIds = listOf(1, 2)

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getExercisesWithSelectedTagsUseCase = GetExercisesWithSelectedTagsUseCase(repository)

        every { repository.getExercisesWithTags() } returns flowOf(testExerciseItems)
    }

    @Test
    fun `GetExercisesWithSelectedTags calls repository_getExercisesWithTags once`() {
        getExercisesWithSelectedTagsUseCase(testTagsIds)

        verify(exactly = 1) { repository.getExercisesWithTags() }
    }

    @Test
    fun `GetExercisesWithSelectedTags filter exercise items correctly`() = runTest {
        val expected = listOf(testExerciseItems[1])
        val actual = getExercisesWithSelectedTagsUseCase(testTagsIds).first()

        assertEquals(expected, actual)
    }
}
