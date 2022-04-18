package com.fiz.mono.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.data.database.dao.TransactionDao
import com.fiz.mono.data.entity.CategoryEntity
import com.fiz.mono.data.entity.TransactionEntity

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryItemDao(): CategoryDao
    abstract fun transactionItemDao(): TransactionDao
}
