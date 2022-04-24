package com.fiz.mono.feature_report.domain.use_case

import com.fiz.mono.core.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetCurrentExpenseUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val formatCurrencyUseCase: FormatCurrencyUseCase
) {

    operator fun invoke(currency: String, date: LocalDate): Flow<String> =
        transactionRepository.getCurrentExpense(date).map {
            formatCurrencyUseCase(
                currency,
                it,
                true
            )
        }

}