package com.fiz.mono.calendar.domain

import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.models.TransactionsDay
import org.threeten.bp.LocalDate

fun getTransactionsForDaysCurrentMonth(allTransactions: List<Transaction>, date: LocalDate): List<TransactionsDay> {
    val result = emptyList<TransactionsDay>().toMutableList()
    result.addAll(getEmptyTransactionDayBeforeCurrentMonth(date))
    val transactionDayForCurrentMonthByDays =
        getTransactionDayForCurrentMonthByDays(allTransactions, date)
    result.addAll(transactionDayForCurrentMonthByDays)
    result.addAll(getEmptyTransactionDayAfterCurrentMonth(date))
    return result
}