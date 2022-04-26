package com.fiz.mono.reminder.data

import android.content.SharedPreferences
import com.fiz.mono.reminder.domain.DataLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    DataLocalDataSource {
    override fun loadMinutes() = sharedPreferences.getString("notify minutes", "") ?: ""

    override fun loadHours() = sharedPreferences.getString("notify hours", "") ?: ""

    override fun saveMinute(minute: String) =
        sharedPreferences.edit().putString("notify minutes", minute).apply()

    override fun saveHour(hours: String) =
        sharedPreferences.edit().putString("notify hours", hours).apply()
}