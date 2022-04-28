package com.fiz.mono.report.ui.monthly

import com.fiz.mono.report.ui.category.TransactionsDataItem
import org.threeten.bp.LocalDate

data class ReportMonthlyUiState(
    val date: LocalDate = LocalDate.now(),
    val currency: String = "$",
    val transactionsForMonth: List<TransactionsDataItem> = listOf(),
    val tabSelectedReport: Int = 0,
    val currentBalance: String = "$0.00",
    val currentIncome: String = "$0.00",
    val currentExpense: String = "-$0.00",
    val currentExpenseIncome: String = "+$0.00",
    val lastBalance: String = "+$0.00"
)