package com.fiz.mono.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiz.mono.R
import com.fiz.mono.core.data.data_source.*
import com.fiz.mono.core.data.repositories.CategoryRepositoryImpl
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.core.util.TimeUtils
import com.fiz.mono.database.AppDatabase
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.dao.TransactionDao
import com.fiz.mono.database.entity.TransactionEntity
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
        categoryStore: CategoryDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(
            categoryStore,
            context.getString(R.string.edit),
            context.getString(R.string.add_more)
        )
    }

    @Provides
    @Singleton
    fun provideTransactionDataSource(database: AppDatabase?): TransactionDataSource {
        return TransactionDataSource(database?.transactionItemDao()!!)
    }

    @Provides
    @Singleton
    fun provideCategoryIconUiStateDataSource(): CategoryIconUiStateDataSource {
        return CategoryIconUiStateDataSource()
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

            val allCategoryExpenseDefault = getAllCategoryExpenseDefault(context)
            val allCategoryIncomeDefault = getAllCategoryIncomeDefault(context)

            val allTransactionsDefault = mutableListOf(
                TransactionEntity(
                    0,
                    TimeUtils.getDate(2022, 2, 24),
                    -5.49,
                    context.getString(R.string.food),
                    "Pizza for lazyday",
                    "food"
                ),
                TransactionEntity(
                    1,
                    TimeUtils.getDate(2022, 2, 24),
                    50.0,
                    context.getString(R.string.freelance),
                    "",
                    "challenge"
                ),
                TransactionEntity(
                    2,
                    TimeUtils.getDate(2022, 2, 24),
                    -13.16,
                    context.getString(R.string.shopping),
                    "New Clothes",
                    "market"
                ),
                TransactionEntity(
                    3,
                    TimeUtils.getDate(2022, 2, 24),
                    1000.0,
                    context.getString(R.string.salary),
                    "Jan",
                    "money"
                ),
                TransactionEntity(
                    4,
                    TimeUtils.getDate(2022, 2, 23),
                    -3.10,
                    context.getString(R.string.food),
                    "Pizza",
                    "food"
                ),
                TransactionEntity(
                    5,
                    TimeUtils.getDate(2022, 2, 23),
                    50.0,
                    context.getString(R.string.loan),
                    "",
                    "user"
                ),
                TransactionEntity(
                    6,
                    TimeUtils.getDate(2022, 2, 20),
                    -17.50,
                    context.getString(R.string.food),
                    "Castrang",
                    "cat"
                ),
                TransactionEntity(
                    7,
                    TimeUtils.getDate(2022, 2, 18),
                    200.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                ),
                TransactionEntity(
                    8,
                    TimeUtils.getDate(2022, 2, 5),
                    10.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                )
            )

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