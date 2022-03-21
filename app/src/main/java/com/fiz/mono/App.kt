package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.database.ItemDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ItemDatabase.getInstance(applicationContext)
    }
}