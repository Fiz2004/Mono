package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.database.AppDatabase
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(applicationContext)
        AndroidThreeTen.init(this)
    }
}