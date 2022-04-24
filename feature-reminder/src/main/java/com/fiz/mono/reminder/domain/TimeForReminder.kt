package com.fiz.mono.reminder.domain

import org.threeten.bp.LocalTime

data class TimeForReminder(
    val hour: Int = 0,
    val minute: Int = 0
) {
    fun isNotEmpty(): Boolean {
        return hour != 0 || minute != 0
    }

    fun getLocalTime(): LocalTime {
        return LocalTime.of(hour, minute)
    }
}