package com.fiz.mono.reminder.data

import android.content.SharedPreferences
import com.fiz.mono.reminder.domain.DataLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    DataLocalDataSource {
    override fun loadMinutes() = sharedPreferences.getInt("notify minutes", 0)

    override fun loadHours() = sharedPreferences.getInt("notify hours", 0)

    override fun saveMinute(minute: Int) =
        sharedPreferences.edit().putInt("notify minutes", minute).apply()

    override fun saveHour(hours: Int) =
        sharedPreferences.edit().putInt("notify hours", hours).apply()
}