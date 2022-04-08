package com.fiz.mono.ui.report.monthly

import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import com.fiz.mono.util.currentUtils
import java.text.SimpleDateFormat
import java.util.*

data class ReportMonthlyUiState(
    val date: Calendar = Calendar.getInstance(),
    val currency: String = "$",
    val isDateChange: Boolean = false,
    val allTransactions: List<TransactionUiState> = listOf(),
    var tabSelectedReport: Int = 0
) {

    val currentBalance: String
        get() =
            currentUtils.getCurrencyFormat(
                currency,
                allTransactions.map { it.value }.fold(0.0) { acc, d -> acc + d },
                false
            )

    val currentIncome: String
        get() = currentUtils.getCurrencyFormat(
            currency,
            getAllTransactionsForMonth(date)
                .filter { it.value > 0 }
                .map { it.value }
                .fold(0.0) { acc, d -> acc + d },
            false
        )

    val currentExpense: String
        get() = currentUtils.getCurrencyFormat(
            currency,
            getAllTransactionsForMonth(date)
                .filter { it.value < 0 }
                .map { it.value }
                .fold(0.0) { acc, d -> acc + d },
            false
        )

    val currentExpenseIncome: String
        get() =
            currentUtils.getCurrencyFormat(
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

    val currentPreviousBalance: String
        get() {
            val datePrevMonth = Calendar.getInstance()
            val currentYear = date.get(Calendar.YEAR)
            val currentMonth = date.get(Calendar.MONTH)
            val currentDay = date.get(Calendar.DATE)
            datePrevMonth.set(currentYear, currentMonth, currentDay)
            datePrevMonth.add(Calendar.MONTH, -1)
            val allTransactionsPrevMonthForMonth =
                getAllTransactionsForMonth(datePrevMonth)
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
            return currentUtils.getCurrencyFormat(
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

    private fun getGroupTransactionsByDays(date: Calendar) =
        getAllTransactionsForMonth(date).groupBy {
            SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.getDefault()
            ).format(it.date.time)
        }


    private fun getAllTransactionsForMonth(
        date: Calendar
    ): List<TransactionUiState> {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)

        val allTransactionsForYear =
            allTransactions.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.getDefault()
                ).format(it.date.time) == currentYear.toString()
            }
        val allTransactionsForMonth =
            allTransactionsForYear.filter {
                SimpleDateFormat(
                    "M",
                    Locale.getDefault()
                ).format(it.date.time) == (currentMonth + 1).toString()
            }
        return allTransactionsForMonth
    }
}