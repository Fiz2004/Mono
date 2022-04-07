package com.fiz.mono

import android.app.Application
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.database.AppDatabase
import com.fiz.mono.data.repositories.CategoryRepository
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {
    val database by lazy { AppDatabase.getDatabase() }
    val categoryStore by lazy {
        CategoryDataSource(
            database?.categoryItemDao()!!,
            resources.getString(R.string.edit),
            resources.getString(R.string.add_more)
        )
    }
    val categoryRepository by lazy {
        CategoryRepository(
            categoryStore,
            resources.getString(R.string.edit),
            resources.getString(R.string.add_more)
        )
    }
    val transactionStore by lazy { TransactionDataSource(database?.transactionItemDao()!!) }
    val categoryIconStore by lazy { CategoryIconUiStateDataSource() }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppDatabase.getInstance(applicationContext)
    }
}