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
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class GetExerciseUseCaseTest {
    private lateinit var repository: ExerciseRepository
    private lateinit var getExerciseUseCase: GetExerciseUseCase

    private val testId = 1L
    private val testExercise = Exercise(1L, 1L, "test_name", "test_desc", listOf(), listOf(), "img_filename", "anim_filename")

    @BeforeEach
    fun setUp() {
        repository = mockk()
        getExerciseUseCase = GetExerciseUseCase(repository)

        every { repository.getExerciseById(any()) } returns flowOf(testExercise)
    }

    @Test
    fun `getExercise calls repository_getExerciseById once`() {
        getExerciseUseCase(testId)

        verify(exactly = 1) { repository.getExerciseById(testId) }
    }

    @Test
    fun `getExercise returns flow correctly`() = runTest {
        val expected = testExercise
        val actual = getExerciseUseCase(testId).first()

        assertEquals(expected, actual)
    }
}