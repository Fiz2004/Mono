package com.fiz.mono.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fiz.mono.data.database.TransactionItemDAO

class TransactionStore(private val transactionItemDao: TransactionItemDAO) {
    var allTransactions: LiveData<List<TransactionItem>> = transactionItemDao.getAll()

    fun getAllTransactionsForInput(): LiveData<List<TransactionItem>> {
        return Transformations.map(allTransactions) {
            val result = emptyList<TransactionItem>().toMutableList()
            result.addAll(it)
            result
        }
    }

    suspend fun insertNewTransaction(newTransaction: TransactionItem) {
        transactionItemDao.insert(newTransaction)
    }
}