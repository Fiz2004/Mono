package com.fiz.mono.data.database

import androidx.room.*
import com.fiz.mono.data.TransactionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionItemDAO {
    @Query("SELECT * FROM TransactionItem")
    fun getAll(): Flow<List<TransactionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionItem: TransactionItem)

    @Update
    suspend fun update(transactionItem: TransactionItem)

    @Delete
    suspend fun delete(transactionItem: TransactionItem?)
}