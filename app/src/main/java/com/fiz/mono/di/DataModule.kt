package com.fiz.mono.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.fiz.mono.R
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.database.AppDatabase
import com.fiz.mono.data.repositories.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.preferences),
            AppCompatActivity.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        @ApplicationContext context: Context,
        categoryStore: CategoryDataSource
    ): CategoryRepository {
        return CategoryRepository(
            categoryStore,
            context.getString(R.string.edit),
            context.getString(R.string.add_more)
        )
    }

    @Provides
    @Singleton
    fun provideCategoryDataSource(database: AppDatabase?): CategoryDataSource {
        return CategoryDataSource(
            database?.categoryItemDao()!!
        )
    }

    @Provides
    @Singleton
    fun provideTransactionDataSource(database: AppDatabase?): TransactionDataSource {
        return TransactionDataSource(database?.transactionItemDao()!!)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase? {
        return AppDatabase.getDatabase()
    }

}