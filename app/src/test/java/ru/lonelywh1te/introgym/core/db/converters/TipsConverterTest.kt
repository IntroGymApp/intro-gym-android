package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.db.converters.TipsConverter

class TipsConverterTest {
    private val tipsConverter = TipsConverter()

    @Test
    fun `convert json string to list`() {
        val jsonString = """
            { "tips" : [ "Строка 1", "Строка 2", "Строка 3" ] }
        """.trimIndent()

        val expected = listOf("Строка 1", "Строка 2", "Строка 3")
        val actual = tipsConverter.stringToListConverter(jsonString)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert list to json string`() {
        val list = listOf("Строка 1", "Строка 2", "Строка 3")

        val expected = """
            {"tips":["Строка 1","Строка 2","Строка 3"]}
        """.trimIndent()
        val actual = tipsConverter.listToStringConverter(list)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert json string with empty array to list`() {
        val jsonString = """
            {"tips":[]}
        """.trimIndent()

        val expected = emptyList<String>()
        val actual = tipsConverter.stringToListConverter(jsonString)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert empty list to json string`() {
        val expected = """
            {"tips":[]}
        """.trimIndent()
        val actual = tipsConverter.listToStringConverter(emptyList())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert list to json string and back returns same data`() {
        val expected = listOf("Строка 1", "Строка 2", "Строка 3")
        val actual = tipsConverter.stringToListConverter(tipsConverter.listToStringConverter(listOf("Строка 1", "Строка 2", "Строка 3")))

        Assertions.assertEquals(expected, actual)
    }

}