package com.fiz.mono.database.repositories

import com.fiz.mono.database.data_source.TransactionLocalDataSource
import com.fiz.mono.database.mapper.toTransaction
import com.fiz.mono.database.mapper.toTransactionEntity
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.repositories.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class TransactionRepositoryImpl(
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionRepository {

    override val allTransactions: Flow<List<Transaction>> =
        transactionLocalDataSource.allTransactions.map {
            it.map { it.toTransaction() }
        }.flowOn(defaultDispatcher)

    override fun getCurrentBalance(): Flow<Double> {
        return allTransactions
            .map {
                it
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
            }
            .flowOn(Dispatchers.Default)
    }

    private fun getAllTransactionsForMonth(
        date: LocalDate
    ): Flow<List<Transaction>> {
        return allTransactions.map {
            it.filter { it.localDate.year == date.year }
                .filter { it.localDate.month == date.month }

        }.flowOn(Dispatchers.Default)
    }

    override fun getCurrentIncome(date: LocalDate): Flow<Double> {
        return getAllTransactionsForMonth(date).map {
            it.map { it.value }
                .filter { it > 0 }
                .fold(0.0) { acc, d -> acc + d }
        }.flowOn(Dispatchers.Default)
    }

    override fun getCurrentExpense(date: LocalDate): Flow<Double> {
        return getAllTransactionsForMonth(date).map {
            it.map { it.value }
                .filter { it < 0 }
                .fold(0.0) { acc, d -> acc + d }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun insertNewTransaction(newTransaction: Transaction) =
        withContext(defaultDispatcher) {
            transactionLocalDataSource.insertNewTransaction(newTransaction.toTransactionEntity())
        }

    override suspend fun delete(transaction: Transaction) = withContext(defaultDispatcher) {
        transactionLocalDataSource.delete(transaction.toTransactionEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) =
        withContext(defaultDispatcher) {
            transactionLocalDataSource.updateTransaction(transaction.toTransactionEntity())
        }
}