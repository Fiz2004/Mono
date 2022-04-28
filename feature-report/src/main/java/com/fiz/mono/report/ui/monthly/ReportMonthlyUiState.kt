package com.fiz.mono.report.ui.monthly

import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.report.ui.category.InfoDay
import com.fiz.mono.report.ui.category.TransactionsDataItem
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

data class ReportMonthlyUiState(
    val date: LocalDate = LocalDate.now(),
    val currency: String = "$",
    val isDateChange: Boolean = false,
    val allTransactions: List<Transaction> = listOf(),
    val tabSelectedReport: Int = 0,
    val currentBalance: String = "$0.00",
    val currentIncome: String = "$0.00",
    val currentExpense: String = "-$0.00",
    val currentExpenseIncome: String = "+$0.00",
    val lastBalance: String = "+$0.00"
) {
    private val dateFormatMonthDayYear = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    val transactionsForAdapter: List<TransactionsDataItem>
        get() {
            var groupTransactions =
                getGroupTransactionsByDays(date)

            groupTransactions = groupTransactions.mapValues {
                when (tabSelectedReport) {
                    0 -> it.value
                    1 -> it.value.filter { it.value < 0 }
                    else -> it.value.filter { it.value > 0 }
                }
            }.filterValues { it.isNotEmpty() }

            groupTransactions = groupTransactions.toSortedMap(compareByDescending { it })

            val items = mutableListOf<TransactionsDataItem>()
            for (transactionsForDay in groupTransactions) {
                val expense =
                    transactionsForDay.value.filter { it.value < 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                val income =
                    transactionsForDay.value.filter { it.value > 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                items += TransactionsDataItem.InfoDayHeaderItem(
                    InfoDay(
                        transactionsForDay.key,
                        expense,
                        income
                    )
                )
                items += transactionsForDay.value.map { TransactionsDataItem.InfoTransactionItem(it) }
            }
            return items
        }

    private fun getGroupTransactionsByDays(date: LocalDate) =
        getAllTransactionsForMonth(date).groupBy {
            dateFormatMonthDayYear.format(it.localDate)
        }


    private fun getAllTransactionsForMonth(
        date: LocalDate
    ): List<Transaction> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
    }
}