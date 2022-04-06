package com.fiz.mono.data.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromPhoto(value: String?): List<String?>? {
        val result = value?.split(";")
        return result?.toList()
    }

    @TypeConverter
    fun listPhotoToPhoto(list: List<String?>): String {
        var result = ""
        list.forEach { result = "$it;" }
        if (result != "")
            result.substring(0, result.length - 1)
        return result
    }
}