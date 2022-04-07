package com.fiz.mono.data.data_source

import com.fiz.mono.data.database.dao.TransactionDao
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.ui.models.TransactionUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

class TransactionDataSource(private val transactionDao: TransactionDao) {
    var allTransactions: Flow<List<TransactionUiState>> =
        transactionDao.getAll().map { it.map { it.toTransactionUiState() } }


    suspend fun insertNewTransaction(newTransaction: Transaction) {
        transactionDao.insert(newTransaction)
    }

    suspend fun deleteAll() {
        allTransactions.first().map {
            it.photo.map path@{
                if (it == null) return@path
                val fdelete = File(it)
                if (fdelete.exists()) {
                    fdelete.delete()
                }
            }
            transactionDao.delete(it.toTransaction())
        }
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun getTransactionByID(transaction: Int): TransactionUiState? {
        return if (transaction != -1)
            allTransactions.first().find { it.id == transaction }?.copy()
        else
            null
    }

    suspend fun getNewId(): Int {
        val lastItem = allTransactions.first().lastOrNull()
        val id = lastItem?.id
        return id?.let { it + 1 } ?: 0
    }
}