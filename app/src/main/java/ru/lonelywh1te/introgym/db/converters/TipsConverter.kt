package ru.lonelywh1te.introgym.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

private const val LOG_TAG = "TipsConverter"

data class TipsConverterResult(
    val tips: List<String>
)

class TipsConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToListConverter(jsonString: String): List<String> {
        val result = gson.fromJson(jsonString, TipsConverterResult::class.java)
        return result.tips
    }

    @TypeConverter
    fun listToStringConverter(list: List<String>): String {
        val result = gson.toJson(list)
        return result
    }
}