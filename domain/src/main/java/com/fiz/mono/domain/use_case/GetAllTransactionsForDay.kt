package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.Transaction
import org.threeten.bp.LocalDate

fun getAllTransactionsForDay(
    allTransactions: List<Transaction>, date: LocalDate
): List<Transaction> {
    val allTransactionsForMonth =
        getAllTransactionsForMonth(allTransactions, date)

    val result = allTransactionsForMonth.filter {
        it.localDate.dayOfMonth == date.dayOfMonth
    }

    return result
}