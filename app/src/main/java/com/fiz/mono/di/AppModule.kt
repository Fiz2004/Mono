package com.fiz.mono.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.data.data_source.CategoryIconsLocalDataSource
import com.fiz.mono.core.data.data_source.CategoryLocalDataSource
import com.fiz.mono.core.data.data_source.TransactionLocalDataSource
import com.fiz.mono.core.data.repositories.CategoryRepositoryImpl
import com.fiz.mono.core.data.repositories.TransactionRepositoryImpl
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.core.domain.repositories.TransactionRepository
import com.fiz.mono.database.AppDatabase
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

private const val NAME_DATABASE = "category_item_database"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    lateinit var database: AppDatabase

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
        categoryLocalStore: CategoryLocalDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(
            categoryLocalStore,
            context.getString(R.string.edit),
        )
    }

    @Provides
    @Singleton
    fun provideTransactionDataSource(database: AppDatabase?): TransactionLocalDataSource {
        return TransactionLocalDataSource(database?.transactionItemDao()!!)
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
    fun provideCategoryIconUiStateDataSource(): CategoryIconsLocalDataSource {
        return CategoryIconsLocalDataSource()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>,
        transactionDaoProvider: Provider<TransactionDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            NAME_DATABASE
        )
            .fallbackToDestructiveMigration()
            .addCallback(dataBaseCallback(context, categoryDaoProvider, transactionDaoProvider))
            .build()
    }

    private fun dataBaseCallback(
        context: Context,
        categoryDaoProvider: Provider<CategoryDao>,
        transactionDaoProvider: Provider<TransactionDao>
    ) = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val allCategoryExpenseDefault = AppDatabase.getAllCategoryExpenseDefault(context)
            val allCategoryIncomeDefault = AppDatabase.getAllCategoryIncomeDefault(context)
            val allTransactionsDefault = AppDatabase.getTransactionsDefault(context)

            CoroutineScope(Dispatchers.Default).launch {
                categoryDaoProvider.get().insertAll(allCategoryExpenseDefault)
                categoryDaoProvider.get().insertAll(allCategoryIncomeDefault)
                transactionDaoProvider.get().insertAll(allTransactionsDefault)
            }
        }
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.categoryItemDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao =
        database.transactionItemDao()

    @Singleton
    @Provides
    fun provideDefaultDispatcher() = Dispatchers.Default
}