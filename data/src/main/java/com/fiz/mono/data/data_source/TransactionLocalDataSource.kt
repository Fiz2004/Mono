package com.fiz.mono.data.data_source

import com.fiz.mono.data.dao.TransactionDao
import com.fiz.mono.data.entity.TransactionEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionLocalDataSourceImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionLocalDataSource {

    override val allTransactions: Flow<List<TransactionEntity>> =
        transactionDao.getAll()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    override fun deleteAll() {
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

    override suspend fun insertNewTransaction(newTransaction: TransactionEntity) =
        withContext(defaultDispatcher) {
            transactionDao.insert(newTransaction)
        }

    override suspend fun delete(transaction: TransactionEntity) = withContext(defaultDispatcher) {
        transactionDao.delete(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) =
        withContext(defaultDispatcher) {
            transactionDao.update(transaction)
        }
}

interface TransactionLocalDataSource {

    val allTransactions: Flow<List<TransactionEntity>>

    fun deleteAll()

    suspend fun insertNewTransaction(newTransaction: TransactionEntity)

    suspend fun delete(transaction: TransactionEntity)

    suspend fun updateTransaction(transaction: TransactionEntity)
}