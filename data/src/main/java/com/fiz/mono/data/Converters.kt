package com.fiz.mono.data

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun fromPhoto(value: String?): List<String?>? {
        val result = value?.split(";")
        return result?.toList()
    }

    @TypeConverter
    fun listPhotoToPhoto(list: List<String?>): String {
        var result = ""
        list.forEach { result += "$it;" }
        if (result != "")
            result = result.substring(0, result.length - 1)
        return result
    }
}