package ru.lonelywh1te.introgym.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

private const val LOG_TAG = "StepsConverter"

data class StepsConverterResult(
    val steps: List<String>
)

class StepsConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToListConverter(jsonString: String): List<String> {
        val result = gson.fromJson(jsonString, StepsConverterResult::class.java)
        return result.steps
    }

    @TypeConverter
    fun listToStringConverter(list: List<String>): String {
        val result = gson.toJson(StepsConverterResult(list))
        return result
    }
}