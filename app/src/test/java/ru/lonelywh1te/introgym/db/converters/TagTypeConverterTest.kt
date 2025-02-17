package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.lonelywh1te.introgym.db.TagType

class TagTypeConverterTest {
    private val tagTypeConverter = TagTypeConverter()

    @Test
    fun `convert tag type enum to string`() {
        val expected = "MUSCLE"
        val actual = tagTypeConverter.toString(TagType.MUSCLE)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to tag type enum`() {
        val expected = TagType.MUSCLE
        val actual = tagTypeConverter.toTagType("MUSCLE")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to tag type enum and back returns same data`() {
        val expected = "MUSCLE"
        val actual = tagTypeConverter.toString(tagTypeConverter.toTagType("MUSCLE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert invalid to tag type enum throws exception`() {
        val invalidTagString = "UNKNOWN"

        assertThrows<IllegalArgumentException> {
            tagTypeConverter.toTagType(invalidTagString)
        }
    }
}