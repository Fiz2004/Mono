package com.fiz.mono.on_boarding.data

import android.content.SharedPreferences
import com.fiz.mono.on_boarding.domain.FirstTimeLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirstTimeLocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    FirstTimeLocalDataSource {
    override fun loadFirstTime() = sharedPreferences.getBoolean(FIRST_TIME, true)

    override fun saveFirstTime(firstTime: Boolean) =
        sharedPreferences.edit().putBoolean(FIRST_TIME, firstTime).apply()

    companion object {
        const val FIRST_TIME = "firstTime"
    }
}