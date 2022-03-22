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

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {

            val allCategoryExpenseDefault = mutableListOf(
                CategoryItem("e0", context.getString(R.string.bank), "bank"),
                CategoryItem("e1", context.getString(R.string.food), "food"),
                CategoryItem("e2", context.getString(R.string.medican), "medican"),
                CategoryItem("e3", context.getString(R.string.gym), "gym"),
                CategoryItem("e4", context.getString(R.string.coffee), "coffee"),
                CategoryItem("e5", context.getString(R.string.shopping), "market"),
                CategoryItem("e6", context.getString(R.string.cats), "cat"),
                CategoryItem("e7", context.getString(R.string.party), "party"),
                CategoryItem("e8", context.getString(R.string.gift), "gift"),
                CategoryItem("e9", context.getString(R.string.gas), "gas"),
            )
            val allCategoryIncomeDefault = mutableListOf(
                CategoryItem("i0", context.getString(R.string.freelance), "challenge"),
                CategoryItem("i1", context.getString(R.string.salary), "money"),
                CategoryItem("i2", context.getString(R.string.bonus), "coin"),
                CategoryItem("i3", context.getString(R.string.loan), "user"),
            )


            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        ItemDatabase::class.java,
                        NAME_DATABASE
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.Default).launch {
                                    allCategoryExpenseDefault.forEach {
                                        INSTANCE?.categoryItemDao()?.insert(it)
                                    }
                                    allCategoryIncomeDefault.forEach {
                                        INSTANCE?.categoryItemDao()?.insert(it)
                                    }
                                }
                            }
                        })
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
