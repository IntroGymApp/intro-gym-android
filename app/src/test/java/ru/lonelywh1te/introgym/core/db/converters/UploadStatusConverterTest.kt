package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.lonelywh1te.introgym.core.db.converters.UploadStatusConverter
import ru.lonelywh1te.introgym.core.db.UploadStatus

class UploadStatusConverterTest {
    private val uploadStatusConverter = UploadStatusConverter()

    @Test
    fun `convert upload status enum to string`() {
        val expected = "COMPLETE"
        val actual = uploadStatusConverter.toString(UploadStatus.COMPLETE)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to upload status enum`() {
        val expected = UploadStatus.COMPLETE
        val actual = uploadStatusConverter.toUploadStatus("COMPLETE")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert string to upload status enum and back returns same data`() {
        val expected = "COMPLETE"
        val actual = uploadStatusConverter.toString(uploadStatusConverter.toUploadStatus("COMPLETE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert invalid to upload status enum throws exception`() {
        val invalidTagString = "UNKNOWN"

        assertThrows<IllegalArgumentException> {
            uploadStatusConverter.toUploadStatus(invalidTagString)
        }
    }

}