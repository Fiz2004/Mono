package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.Transaction
import org.threeten.bp.LocalDate

fun getAllTransactionsForMonth(
    allTransactions: List<Transaction>, date: LocalDate
): List<Transaction> {
    return allTransactions.filter { it.localDate.year == date.year }
        .filter { it.localDate.month == date.month }
}