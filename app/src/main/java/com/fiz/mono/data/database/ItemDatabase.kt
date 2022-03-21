package com.fiz.mono.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.TransactionItem

@Database(entities = [CategoryItem::class, TransactionItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun categoryItemDao(): CategoryItemDAO?
    abstract fun transactionItemDao(): TransactionItemDAO?

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
                        "category_item_database"
                    )
                        .fallbackToDestructiveMigration()
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
