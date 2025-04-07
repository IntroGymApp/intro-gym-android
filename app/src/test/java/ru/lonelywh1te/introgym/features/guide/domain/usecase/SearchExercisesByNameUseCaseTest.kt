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

class SearchExercisesByNameUseCaseTest {
    private lateinit var repository: ExerciseRepository
    private lateinit var searchExercisesByNameUseCase: SearchExercisesByNameUseCase

    private val testQuery = "TEST"

    private val testExerciseItems = listOf(
        ExerciseItem(1L, "test_name", 1L, listOf(1, 1), "img_filename"),
        ExerciseItem(1L, "name", 2L, listOf(1, 1), "img_filename"),
        ExerciseItem(1L, "name_test", 3L, listOf(1, 1), "img_filename")

    )

    @BeforeEach
    fun setUp() {
        repository = mockk()
        searchExercisesByNameUseCase = SearchExercisesByNameUseCase(repository)

        every { repository.getExercisesWithTags() } returns flowOf(testExerciseItems)
    }

    @Test
    fun `SearchExercisesByName calls repository_getExercisesWithTags once`() {
        searchExercisesByNameUseCase(testQuery)

        verify(exactly = 1) { repository.getExercisesWithTags() }
    }

    @Test
    fun `SearchExercisesByName filter exercises by name correctly`() = runTest {
        val expected = testExerciseItems.filter { it.name.contains(testQuery, true) }
        val actual = searchExercisesByNameUseCase(testQuery).first()

        assertEquals(expected, actual)
    }

}