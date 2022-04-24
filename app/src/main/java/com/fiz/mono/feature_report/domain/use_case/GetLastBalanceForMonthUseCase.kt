package com.fiz.mono.feature_report.domain.use_case

import com.fiz.mono.core.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetLastBalanceForMonthUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val formatCurrencyUseCase: FormatCurrencyUseCase,

    ) {

    operator fun invoke(currency: String, date: LocalDate): Flow<String> =
        transactionRepository.getCurrentIncome(date.minusMonths(1))
            .zip(transactionRepository.getCurrentExpense(date.minusMonths(1))) { a, b ->
                a + b
            }.map {
                formatCurrencyUseCase(
                    currency,
                    it,
                    true
                )
            }
}