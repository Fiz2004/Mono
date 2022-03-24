package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.data.database.ItemDatabase

class App : Application() {
    val database by lazy { ItemDatabase.getDatabase() }
    val categoryStore by lazy { CategoryStore(database?.categoryItemDao()!!) }
    val transactionStore by lazy { TransactionStore(database?.transactionItemDao()!!) }

    override fun onCreate() {
        super.onCreate()
        ItemDatabase.getInstance(applicationContext)
    }
}