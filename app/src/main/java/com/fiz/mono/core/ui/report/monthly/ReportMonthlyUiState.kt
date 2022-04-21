package com.fiz.mono.core.ui.report.monthly

import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.core.ui.shared_adapters.InfoDay
import com.fiz.mono.core.ui.shared_adapters.TransactionsDataItem
import com.fiz.mono.core.util.CurrentUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

data class ReportMonthlyUiState(
    val date: LocalDate = LocalDate.now(),
    val currency: String = "$",
    val isDateChange: Boolean = false,
    val allTransactions: List<Transaction> = listOf(),
    var tabSelectedReport: Int = 0
) {

    private val dateFormatMonthDayYear = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    val currentBalance: String
        get() =
            CurrentUtils.getCurrencyFormat(
                currency,
                allTransactions.map { it.value }.fold(0.0) { acc, d -> acc + d },
                false
            )

    val currentIncome: String
        get() = CurrentUtils.getCurrencyFormat(
            currency,
            getAllTransactionsForMonth(date)
                .filter { it.value > 0 }
                .map { it.value }
                .fold(0.0) { acc, d -> acc + d },
            false
        )

    val currentExpense: String
        get() = CurrentUtils.getCurrencyFormat(
            currency,
            getAllTransactionsForMonth(date)
                .filter { it.value < 0 }
                .map { it.value }
                .fold(0.0) { acc, d -> acc + d },
            false
        )

    val currentExpenseIncome: String
        get() =
            CurrentUtils.getCurrencyFormat(
                currency,
                getAllTransactionsForMonth(date)
                    .filter { it.value > 0 }
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
                        +
                        getAllTransactionsForMonth(date)
                            .filter { it.value < 0 }
                            .map { it.value }
                            .fold(0.0) { acc, d -> acc + d },
                true
            )

    val previousBalance: String
        get() {
            val allTransactionsPrevMonthForMonth =
                getAllTransactionsForMonth(date.minusMonths(1))
            val prevIncome =
                allTransactionsPrevMonthForMonth
                    .filter { it.value > 0 }
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
            val prevExpense =
                allTransactionsPrevMonthForMonth
                    .filter { it.value < 0 }
                    .map { it.value }
                    .fold(0.0) { acc, d -> acc + d }
            return CurrentUtils.getCurrencyFormat(
                currency,
                prevIncome + prevExpense,
                false
            )
        }

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