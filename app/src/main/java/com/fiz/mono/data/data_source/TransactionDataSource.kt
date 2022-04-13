package com.fiz.mono.data.data_source

import com.fiz.mono.data.database.dao.TransactionDao
import com.fiz.mono.data.entity.TransactionEntity
import com.fiz.mono.ui.models.TransactionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class TransactionDataSource(private val transactionDao: TransactionDao) {
    var allTransactions: Flow<List<TransactionUiState>> =
        transactionDao.getAll().map { it.map { it.toTransactionUiState() } }


    suspend fun insertNewTransaction(newTransaction: TransactionEntity) {
        transactionDao.insert(newTransaction)
    }

    fun deleteAll() {
        CoroutineScope(Dispatchers.Default).launch {
            allTransactions.first().map {
                it.photo.map path@{
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

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }
}