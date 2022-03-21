package com.fiz.mono.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fiz.mono.data.CategoryItem

@Database(entities = [CategoryItem::class], version = 1)
abstract class CategoryItemDatabase : RoomDatabase() {
    abstract fun expenseCategoryItemDao(): CategoryItemDAO?

    companion object {

        @Volatile
        private var INSTANCE: CategoryItemDatabase? = null

        fun getInstance(context: Context): CategoryItemDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        CategoryItemDatabase::class.java,
                        "category_item_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        fun getDatabase(): CategoryItemDatabase? {
            return INSTANCE
        }
    }
}
