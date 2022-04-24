package com.fiz.mono.core.data.data_source

import com.fiz.mono.database.dao.TransactionDao
import com.fiz.mono.database.entity.TransactionEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import java.io.File

class TransactionLocalDataSource(
    private val transactionDao: TransactionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    val allTransactions: Flow<List<TransactionEntity>> =
        transactionDao.getAll()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    fun deleteAll() {
        CoroutineScope(defaultDispatcher)
            .launch {
                allTransactions.first().map {
                    it.photoPaths.map path@{
                        if (it == null) return@path
                        val fdelete = File(it)
                        if (fdelete.exists()) {
                            fdelete.delete()
                        }
                    }
                }
                transactionDao.deleteAll()
            }
    }

    suspend fun insertNewTransaction(newTransaction: TransactionEntity) =
        withContext(defaultDispatcher) {
            transactionDao.insert(newTransaction)
        }

    suspend fun delete(transaction: TransactionEntity) = withContext(defaultDispatcher) {
        transactionDao.delete(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(defaultDispatcher) {
        transactionDao.update(transaction)
    }
}