package com.fiz.mono.report.domain

import com.fiz.mono.domain.repositories.TransactionRepository
import com.fiz.mono.domain.use_case.FormatCurrencyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val formatCurrencyUseCase: FormatCurrencyUseCase
) {
    operator fun invoke(currency: String): Flow<String> =
        transactionRepository.getCurrentBalance().map {
            formatCurrencyUseCase(
                currency,
                it,
                false
            )
        }

}