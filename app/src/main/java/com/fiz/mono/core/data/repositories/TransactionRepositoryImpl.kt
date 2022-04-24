package com.fiz.mono.core.data.repositories

import com.fiz.mono.core.data.data_source.TransactionLocalDataSource
import com.fiz.mono.core.data.mapper.toTransaction
import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.core.domain.repositories.TransactionRepository
import com.fiz.mono.database.entity.TransactionEntity
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

    override suspend fun insertNewTransaction(newTransaction: TransactionEntity) =
        withContext(defaultDispatcher) {
            transactionLocalDataSource.insertNewTransaction(newTransaction)
        }

    override suspend fun delete(transaction: TransactionEntity) = withContext(defaultDispatcher) {
        transactionLocalDataSource.delete(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) =
        withContext(defaultDispatcher) {
            transactionLocalDataSource.updateTransaction(transaction)
        }
}