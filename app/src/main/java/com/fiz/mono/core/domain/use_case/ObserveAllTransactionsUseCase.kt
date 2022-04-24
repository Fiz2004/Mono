package com.fiz.mono.core.domain.use_case

import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.core.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.allTransactions
    }

}