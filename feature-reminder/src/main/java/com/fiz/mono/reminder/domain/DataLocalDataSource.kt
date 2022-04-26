package com.fiz.mono.reminder.domain

interface DataLocalDataSource {
    fun loadMinutes(): String

    fun loadHours(): String

    fun saveMinute(minute: String)

    fun saveHour(hours: String)
}