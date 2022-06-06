package com.fiz.mono.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.data.AppDatabase
import com.fiz.mono.data.dao.CategoryDao
import com.fiz.mono.data.dao.TransactionDao
import com.fiz.mono.data.data_source.CategoryIconsLocalDataSourceImpl
import com.fiz.mono.data.data_source.CategoryLocalDataSource
import com.fiz.mono.data.data_source.TransactionLocalDataSource
import com.fiz.mono.data.repositories.CategoryIconsRepositoryImpl
import com.fiz.mono.data.repositories.CategoryRepositoryImpl
import com.fiz.mono.data.repositories.TransactionRepositoryImpl
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.TransactionRepository
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
    fun provideCategoryIconUiStateDataSource(): CategoryIconsLocalDataSourceImpl {
        return CategoryIconsLocalDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideCategoryCategoryIconsRepository(categoryIconsLocalDataSourceImpl: CategoryIconsLocalDataSourceImpl): CategoryIconsRepositoryImpl {
        return CategoryIconsRepositoryImpl(categoryIconsLocalDataSourceImpl)
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

}