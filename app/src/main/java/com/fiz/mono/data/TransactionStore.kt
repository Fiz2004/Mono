package com.fiz.mono.data

import androidx.lifecycle.LiveData
import com.fiz.mono.data.database.TransactionItemDAO

class TransactionStore(private val transactionItemDao: TransactionItemDAO) {
    var allTransactions: LiveData<List<TransactionItem>> = transactionItemDao.getAll()

    suspend fun insertNewTransaction(newTransaction: TransactionItem) {
        transactionItemDao.insert(newTransaction)
    }
}