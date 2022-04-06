package com.fiz.mono.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.data.database.dao.TransactionDao
import com.fiz.mono.data.entity.Category
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.util.TimeUtils.getDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NAME_DATABASE = "category_item_database"

@Database(
    entities = [Category::class, Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryItemDao(): CategoryDao
    abstract fun transactionItemDao(): TransactionDao

    private class ItemDatabaseCallback(val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val allCategoryExpenseDefault = mutableListOf(
                Category("e0", context.getString(R.string.bank), "bank"),
                Category("e1", context.getString(R.string.food), "food"),
                Category(
                    "e2",
                    context.getString(R.string.medican),
                    "medican"
                ),
                Category("e3", context.getString(R.string.gym), "gym"),
                Category(
                    "e4",
                    context.getString(R.string.coffee),
                    "coffee"
                ),
                Category(
                    "e5",
                    context.getString(R.string.shopping),
                    "market"
                ),
                Category("e6", context.getString(R.string.cats), "cat"),
                Category("e7", context.getString(R.string.party), "party"),
                Category("e8", context.getString(R.string.gift), "gift"),
                Category("e9", context.getString(R.string.gas), "gas"),
            )
            val allCategoryIncomeDefault = mutableListOf(
                Category(
                    "i0",
                    context.getString(R.string.freelance),
                    "challenge"
                ),
                Category("i1", context.getString(R.string.salary), "money"),
                Category("i2", context.getString(R.string.bonus), "coin"),
                Category("i3", context.getString(R.string.loan), "user"),
            )

            val allTransactionsDefault = mutableListOf(
                Transaction(
                    0,
                    getDate(2022, 1, 24),
                    -5.49,
                    context.getString(R.string.food),
                    "Pizza for lazyday",
                    "food"
                ),
                Transaction(
                    1,
                    getDate(2022, 1, 24),
                    50.0,
                    context.getString(R.string.freelance),
                    "",
                    "challenge"
                ),
                Transaction(
                    2,
                    getDate(2022, 1, 24),
                    -13.16,
                    context.getString(R.string.shopping),
                    "New Clothes",
                    "market"
                ),
                Transaction(
                    3,
                    getDate(2022, 1, 24),
                    1000.0,
                    context.getString(R.string.salary),
                    "Jan",
                    "money"
                ),
                Transaction(
                    4,
                    getDate(2022, 1, 23),
                    -3.10,
                    context.getString(R.string.food),
                    "Pizza",
                    "food"
                ),
                Transaction(
                    5,
                    getDate(2022, 1, 23),
                    50.0,
                    context.getString(R.string.loan),
                    "",
                    "user"
                ),
                Transaction(
                    6,
                    getDate(2022, 1, 20),
                    -17.50,
                    context.getString(R.string.food),
                    "Castrang",
                    "cat"
                ),
                Transaction(
                    7,
                    getDate(2022, 1, 18),
                    200.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                ),
                Transaction(
                    8,
                    getDate(2022, 1, 5),
                    10.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                )
            )

            CoroutineScope(Dispatchers.Default).launch {
                INSTANCE?.categoryItemDao()?.insertAll(allCategoryExpenseDefault)
                INSTANCE?.categoryItemDao()?.insertAll(allCategoryIncomeDefault)
                INSTANCE?.transactionItemDao()?.insertAll(allTransactionsDefault)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        NAME_DATABASE
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(ItemDatabaseCallback(context))
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        fun getDatabase(): AppDatabase? {
            return INSTANCE
        }
    }
}
