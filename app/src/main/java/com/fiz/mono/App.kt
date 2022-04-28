package com.fiz.mono

import android.app.Application
import com.fiz.mono.reminder.receiver.GetMainActiviti
import com.fiz.mono.ui.MainActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), GetMainActiviti {
    override fun onCreate() {
        super.onCreate()
        // For LocalDate
        AndroidThreeTen.init(this)
    }

    override fun get(): Class<*> {
        return MainActivity::class.java
    }
}