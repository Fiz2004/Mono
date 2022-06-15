package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.Transaction
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface TransactionRepository {
    val observeTransactions: Flow<List<Transaction>>

    suspend fun getTransactions(): List<Transaction>

    fun getCurrentBalance(): Flow<Double>

    fun getCurrentIncome(date: LocalDate): Flow<Double>

    fun getCurrentExpense(date: LocalDate): Flow<Double>

    suspend fun insertNewTransaction(newTransaction: Transaction)

    suspend fun delete(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)
}