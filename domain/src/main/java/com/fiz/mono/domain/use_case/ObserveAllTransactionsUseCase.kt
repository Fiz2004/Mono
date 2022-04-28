package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.allTransactions
    }

}