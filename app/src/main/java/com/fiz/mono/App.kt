package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.AppDatabase
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {
    val database by lazy { AppDatabase.getDatabase() }
    val categoryStore by lazy { CategoryStore(database?.categoryItemDao()!!) }
    val transactionStore by lazy { TransactionStore(database?.transactionItemDao()!!) }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppDatabase.getInstance(applicationContext)
    }
}