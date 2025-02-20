package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.db.converters.LocalDateConverter
import java.time.LocalDate

class LocalDateConverterTest {
    private val converter = LocalDateConverter()

    @Test
    fun `convert localdate to string`() {
        val expected = "2025-01-01"
        val actual = converter.toString(LocalDate.of(2025, 1, 1))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to localdate`() {
        val expected = LocalDate.of(2025, 1, 1)
        val actual = converter.toLocalDate("2025-01-01")

        Assertions.assertEquals(expected, actual)

    }

    @Test
    fun `convert localdate to string and back returns same date`() {
        val expected = LocalDate.of(2025, 1, 1)
        val actual = converter.toLocalDate(converter.toString(LocalDate.of(2025, 1, 1)))

        Assertions.assertEquals(expected, actual)
    }

}