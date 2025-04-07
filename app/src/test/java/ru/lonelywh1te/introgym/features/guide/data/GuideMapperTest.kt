package ru.lonelywh1te.introgym.features.guide.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseCategoryWithCount
import ru.lonelywh1te.introgym.data.db.model.ExerciseEntityWithTags
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

class GuideMapperTest {
    private val testExerciseEntity = ExerciseEntity(1L, 1L, "test_name", "test_desc", listOf(), listOf(), "img_filename", "anim_filename")
    private val testExerciseCategoryWithCount = ExerciseCategoryWithCount(1, "test_name", 1, "img_filename")
    private val testTagEntity = TagEntity(1, "test_name", TagType.OTHER)
    private val testExerciseEntityWithTags = ExerciseEntityWithTags(testExerciseEntity, listOf(testTagEntity, testTagEntity))

    private val testExerciseExpected = Exercise(1L, 1L, "test_name", "test_desc", listOf(), listOf(), "img_filename", "anim_filename")
    private val testExerciseCategoryExpected = ExerciseCategoryItem(1, "test_name", 1, "img_filename")
    private val testTagExpected = Tag(1, "test_name", TagType.OTHER)
    private val testExerciseItemExpected = ExerciseItem(1L, "test_name", 1L, listOf(1, 1), "img_filename")

    @Test
    fun `ExerciseCategoryWithCount to ExerciseCategoryItem map correctly`() {
        val expected = testExerciseCategoryExpected
        val actual = testExerciseCategoryWithCount.toExerciseCategoryItem()

        assertEquals(expected, actual)
    }

    @Test
    fun `ExerciseEntityWithTags to ExerciseItem map correctly`() {
        val expected = testExerciseItemExpected
        val actual = testExerciseEntityWithTags.toExerciseItem()

        assertEquals(expected, actual)
    }

    @Test
    fun `ExerciseEntity to Exercise map correctly`() {
        val expected = testExerciseExpected
        val actual = testExerciseEntity.toExercise()

        assertEquals(expected, actual)
    }

    @Test
    fun `TagEntity to Tag map correctly`() {
        val expected = testTagExpected
        val actual = testTagEntity.toTag()

        assertEquals(expected, actual)
    }

}