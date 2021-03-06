package com.fiz.mono.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.data.AppDatabase
import com.fiz.mono.data.dao.CategoryDao
import com.fiz.mono.data.dao.TransactionDao
import com.fiz.mono.data.data_source.TransactionLocalDataSource
import com.fiz.mono.data.repositories.TransactionRepositoryImpl
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val NAME_DATABASE = "category_item_database"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

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
    fun provideTransactionRepository(
        transactionLocalDataSource: TransactionLocalDataSource
    ): TransactionRepository {
        return TransactionRepositoryImpl(
            transactionLocalDataSource
        )
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            NAME_DATABASE
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.categoryItemDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao =
        database.transactionItemDao()

}