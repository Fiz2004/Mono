package com.fiz.mono.core.domain.repositories

import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface TransactionRepository {
    val allTransactions: Flow<List<Transaction>>

    fun getCurrentBalance(): Flow<Double>

    fun getCurrentIncome(date: LocalDate): Flow<Double>

    fun getCurrentExpense(date: LocalDate): Flow<Double>

    suspend fun insertNewTransaction(newTransaction: TransactionEntity)

    suspend fun delete(transaction: TransactionEntity)

    suspend fun updateTransaction(transaction: TransactionEntity)
}