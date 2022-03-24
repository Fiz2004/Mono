package com.fiz.mono.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.util.TimeUtils.getDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NAME_DATABASE = "category_item_database"

@Database(
    entities = [CategoryItem::class, TransactionItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun categoryItemDao(): CategoryItemDAO
    abstract fun transactionItemDao(): TransactionItemDAO

    private class ItemDatabaseCallback(val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val allCategoryExpenseDefault = mutableListOf(
                CategoryItem("e0", context.getString(R.string.bank), "bank"),
                CategoryItem("e1", context.getString(R.string.food), "food"),
                CategoryItem(
                    "e2",
                    context.getString(R.string.medican),
                    "medican"
                ),
                CategoryItem("e3", context.getString(R.string.gym), "gym"),
                CategoryItem(
                    "e4",
                    context.getString(R.string.coffee),
                    "coffee"
                ),
                CategoryItem(
                    "e5",
                    context.getString(R.string.shopping),
                    "market"
                ),
                CategoryItem("e6", context.getString(R.string.cats), "cat"),
                CategoryItem("e7", context.getString(R.string.party), "party"),
                CategoryItem("e8", context.getString(R.string.gift), "gift"),
                CategoryItem("e9", context.getString(R.string.gas), "gas"),
            )
            val allCategoryIncomeDefault = mutableListOf(
                CategoryItem(
                    "i0",
                    context.getString(R.string.freelance),
                    "challenge"
                ),
                CategoryItem("i1", context.getString(R.string.salary), "money"),
                CategoryItem("i2", context.getString(R.string.bonus), "coin"),
                CategoryItem("i3", context.getString(R.string.loan), "user"),
            )

            val allTransactionsDefault = mutableListOf(
                TransactionItem(
                    0,
                    getDate(2022, 1, 24),
                    -5.49,
                    context.getString(R.string.food),
                    "Pizza for lazyday",
                    "food"
                ),
                TransactionItem(
                    1,
                    getDate(2022, 1, 24),
                    50.0,
                    context.getString(R.string.freelance),
                    "",
                    "challenge"
                ),
                TransactionItem(
                    2,
                    getDate(2022, 1, 24),
                    -13.16,
                    context.getString(R.string.shopping),
                    "New Clothes",
                    "market"
                ),
                TransactionItem(
                    3,
                    getDate(2022, 1, 24),
                    1000.0,
                    context.getString(R.string.salary),
                    "Jan",
                    "money"
                ),
                TransactionItem(
                    4,
                    getDate(2022, 1, 23),
                    -3.10,
                    context.getString(R.string.food),
                    "Pizza",
                    "food"
                ),
                TransactionItem(
                    5,
                    getDate(2022, 1, 23),
                    50.0,
                    context.getString(R.string.loan),
                    "",
                    "user"
                ),
                TransactionItem(
                    6,
                    getDate(2022, 1, 20),
                    -17.50,
                    context.getString(R.string.food),
                    "Castrang",
                    "cat"
                ),
                TransactionItem(
                    7,
                    getDate(2022, 1, 18),
                    200.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                ),
                TransactionItem(
                    8,
                    getDate(2022, 1, 5),
                    10.0,
                    context.getString(R.string.bonus),
                    "Project bonus",
                    "coin"
                )
            )

            CoroutineScope(Dispatchers.Default).launch {
                allCategoryExpenseDefault.forEach {
                    INSTANCE?.categoryItemDao()?.insert(it)
                }
                allCategoryIncomeDefault.forEach {
                    INSTANCE?.categoryItemDao()?.insert(it)
                }
                allTransactionsDefault.forEach {
                    INSTANCE?.transactionItemDao()?.insert(it)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        ItemDatabase::class.java,
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

        fun getDatabase(): ItemDatabase? {
            return INSTANCE
        }
    }
}
