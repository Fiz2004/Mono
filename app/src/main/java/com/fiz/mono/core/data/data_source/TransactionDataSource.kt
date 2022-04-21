package com.fiz.mono.core.data.data_source

import com.fiz.mono.core.data.mapper.toTransaction
import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.database.dao.TransactionDao
import com.fiz.mono.database.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class TransactionDataSource(private val transactionDao: TransactionDao) {
    val scope = CoroutineScope(Dispatchers.Default)

    var allTransactions: Flow<List<Transaction>> =
        transactionDao.getAll().map { it.map { it.toTransaction() } }
            .stateIn(
                scope = scope,
                started = WhileSubscribed(5000),
                initialValue = listOf()
            )


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