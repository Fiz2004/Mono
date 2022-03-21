package com.fiz.mono.data.database

import androidx.room.*
import com.fiz.mono.data.TransactionItem

@Dao
interface TransactionItemDAO {
    @Query("SELECT * FROM TransactionItem")
    suspend fun getAll(): List<TransactionItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transactionItem: TransactionItem)

    @Delete
    fun delete(transactionItem: TransactionItem?)
}