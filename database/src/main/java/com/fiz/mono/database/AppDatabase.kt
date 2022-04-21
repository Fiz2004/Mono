package com.fiz.mono.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.dao.TransactionDao
import com.fiz.mono.database.entity.CategoryEntity
import com.fiz.mono.database.entity.TransactionEntity

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
