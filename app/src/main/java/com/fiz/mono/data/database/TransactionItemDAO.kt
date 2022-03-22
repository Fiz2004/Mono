package com.fiz.mono.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiz.mono.data.TransactionItem

@Dao
interface TransactionItemDAO {
    @Query("SELECT * FROM TransactionItem")
    fun getAll(): LiveData<List<TransactionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionItem: TransactionItem)

    @Delete
    suspend fun delete(transactionItem: TransactionItem?)
}