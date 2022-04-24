package com.fiz.mono.reminder.domain

interface DataLocalDataSource {
    fun loadMinutes(): Int

    fun loadHours(): Int

    fun saveMinute(minute: Int)

    fun saveHour(hours: Int)
}