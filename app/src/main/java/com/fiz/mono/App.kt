package com.fiz.mono

import android.app.Application
import com.fiz.mono.reminder.receiver.GetMainActivity
import com.fiz.mono.ui.MainActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), GetMainActivity {
    override fun onCreate() {
        super.onCreate()

        // For LocalDate
        AndroidThreeTen.init(this)
    }

    override fun get(): Class<*> {
        return MainActivity::class.java
    }
}