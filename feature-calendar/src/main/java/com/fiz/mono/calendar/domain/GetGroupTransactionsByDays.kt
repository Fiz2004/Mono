package com.fiz.mono.calendar.domain

import com.fiz.mono.domain.models.Transaction
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

private val dateFormatDay = DateTimeFormatter.ofPattern("dd")

fun getGroupTransactionsByDays(allTransactions: List<Transaction>, date: LocalDate) =
    getAllTransactionsForMonth(allTransactions, date).groupBy {
        dateFormatDay.format(it.localDate)
    }