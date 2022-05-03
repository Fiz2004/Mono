package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetCurrentIncomeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val formatCurrencyUseCase: FormatCurrencyUseCase
) {

    operator fun invoke(currency: String, date: LocalDate): Flow<String> =
        transactionRepository.getCurrentIncome(date).map {
            formatCurrencyUseCase(
                currency,
                it,
                false
            )
        }

}