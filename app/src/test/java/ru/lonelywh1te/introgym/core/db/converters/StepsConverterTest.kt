package ru.lonelywh1te.introgym.db.converters

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.db.converters.StepsConverter

class StepsConverterTest {
    private val stepsConverter = StepsConverter()

    @Test
    fun `convert json string to list`() {
        val jsonString = """
            { "steps" : [ "Строка 1", "Строка 2", "Строка 3" ] }
        """.trimIndent()

        val expected = listOf("Строка 1", "Строка 2", "Строка 3")
        val actual = stepsConverter.stringToListConverter(jsonString)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert list to json string`() {
        val list = listOf("Строка 1", "Строка 2", "Строка 3")

        val expected = """
            {"steps":["Строка 1","Строка 2","Строка 3"]}
        """.trimIndent()
        val actual = stepsConverter.listToStringConverter(list)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert json string with empty array to list`() {
        val jsonString = """
            {"steps":[]}
        """.trimIndent()

        val expected = emptyList<String>()
        val actual = stepsConverter.stringToListConverter(jsonString)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert empty list to json string`() {
        val expected = """
            {"steps":[]}
        """.trimIndent()
        val actual = stepsConverter.listToStringConverter(emptyList())

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `convert list to json string and back returns same data`() {
        val expected = listOf("Строка 1", "Строка 2", "Строка 3")
        val actual = stepsConverter.stringToListConverter(stepsConverter.listToStringConverter(listOf("Строка 1", "Строка 2", "Строка 3")))

        Assertions.assertEquals(expected, actual)
    }

}