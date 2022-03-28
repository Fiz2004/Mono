package com.fiz.mono.ui.report.monthly

import androidx.lifecycle.ViewModel
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.shared_adapters.InfoDay
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.util.*

class ReportMonthlyViewModel(private val transactionStore: TransactionStore) : ViewModel() {
    val allTransactions = transactionStore.allTransactions

    var tabSelectedReport: Int = 0

    fun getCurrentBalance(): Double {
        return allTransactions.value?.map { it.value }?.fold(0.0) { acc, d -> acc + d } ?: 0.0
    }

    fun getCurrentIncome(date: Calendar): Double {
        val allTransactionsForMonth =
            transactionStore.getAllTransactionsForMonth(date)

        return allTransactionsForMonth?.filter { it.value > 0 }?.map { it.value }
            ?.fold(0.0) { acc, d -> acc + d } ?: 0.0
    }

    fun getCurrentExpense(date: Calendar): Double {
        val allTransactionsForMonth =
            transactionStore.getAllTransactionsForMonth(date)

        return allTransactionsForMonth?.filter { it.value < 0 }?.map { it.value }
            ?.fold(0.0) { acc, d -> acc + d } ?: 0.0
    }

    fun getPreviousBalanceValue(date: Calendar): Double {
        val datePrevMonth = Calendar.getInstance()
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH)
        val currentDay = date.get(Calendar.DATE)
        datePrevMonth.set(currentYear, currentMonth, currentDay)
        datePrevMonth.add(Calendar.MONTH, -1)

        val allTransactionsPrevMonthForMonth =
            transactionStore.getAllTransactionsForMonth(datePrevMonth)

        val prevIncome = allTransactionsPrevMonthForMonth?.filter { it.value > 0 }?.map { it.value }
            ?.fold(0.0) { acc, d -> acc + d } ?: 0.0
        val prevExpense =
            allTransactionsPrevMonthForMonth?.filter { it.value < 0 }?.map { it.value }
                ?.fold(0.0) { acc, d -> acc + d } ?: 0.0

        return prevIncome + prevExpense
    }

    fun getTransactions(tabSelectedReport: Int, date: Calendar): MutableList<TransactionsDataItem> {
        var groupTransactions =
            transactionStore.getGroupTransactionsByDays(date, "MMM dd, yyyy")

        groupTransactions = groupTransactions?.mapValues {
            when (tabSelectedReport) {
                0 -> it.value
                1 -> it.value.filter { it.value < 0 }
                else -> it.value.filter { it.value > 0 }
            }
        }?.filterValues { it.isNotEmpty() }

        groupTransactions = groupTransactions?.toSortedMap(compareByDescending { it })

        val items = mutableListOf<TransactionsDataItem>()
        if (groupTransactions != null) {
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
        }
        return items
    }
}
