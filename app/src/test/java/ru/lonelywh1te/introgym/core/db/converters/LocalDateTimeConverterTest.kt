package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.db.converters.LocalDateTimeConverter
import java.time.LocalDateTime

class LocalDateTimeConverterTest {
    private val converter = LocalDateTimeConverter()

    @Test
    fun `convert localdatetime to string`() {
        val expected = "2025-01-01T20:15:30"
        val actual = converter.toString(LocalDateTime.of(2025, 1, 1, 20, 15, 30))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to localdatetime`() {
        val expected = LocalDateTime.of(2025, 1, 1, 20, 15, 30)
        val actual = converter.toLocalDateTime("2025-01-01T20:15:30")

        Assertions.assertEquals(expected, actual)

    }

    @Test
    fun `convert localdatetime to string and back returns same date`() {
        val expected = LocalDateTime.of(2025, 1, 1, 20, 15, 30)
        val actual = converter.toLocalDateTime(converter.toString(LocalDateTime.of(2025, 1, 1, 20, 15, 30)))

        Assertions.assertEquals(expected, actual)
    }
}