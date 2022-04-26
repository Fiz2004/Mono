package com.fiz.mono.reminder.domain.models

import org.threeten.bp.LocalTime

data class TimeForReminder(
    val hour: String = "",
    val minute: String = ""
) {
    fun isNotEmpty(): Boolean {
        return hour.isNotBlank() && minute.isNotBlank()
    }

    fun getLocalTime(): LocalTime {
        return LocalTime.of(hour.toInt(), minute.toInt())
    }
}