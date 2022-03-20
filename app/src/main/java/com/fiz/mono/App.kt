package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.database.CategoryItemDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CategoryItemDatabase.getInstance(applicationContext)
        CategoryStore.init()
    }
}