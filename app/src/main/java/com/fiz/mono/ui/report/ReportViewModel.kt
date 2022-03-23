package com.fiz.mono.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import java.text.SimpleDateFormat
import java.util.*

class ReportViewModel(private val transactionStore: TransactionStore) : ViewModel() {
    val allTransactions = transactionStore.allTransactions

    fun getCurrentBalance(): Double {
        return allTransactions.value?.map { it.value }?.fold(0.0) { acc, d -> acc + d } ?: 0.0
    }

    fun getCurrentIncome(date: Calendar): Double {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH) + 1
        val allTransactionsForYear =
            allTransactions.value?.filter { SimpleDateFormat("yyyy").format(it.date.time) == currentYear.toString() } as MutableList<TransactionItem>
        val allTransactionsForMonth =
            allTransactionsForYear.filter { SimpleDateFormat("M").format(it.date.time) == currentMonth.toString() } as MutableList<TransactionItem>

        return allTransactionsForMonth.filter { it.value > 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }
    }

    fun getCurrentExpense(date: Calendar): Double {
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH) + 1
        val allTransactionsForYear =
            allTransactions.value?.filter { SimpleDateFormat("yyyy").format(it.date.time) == currentYear.toString() } as MutableList<TransactionItem>
        val allTransactionsForMonth =
            allTransactionsForYear.filter { SimpleDateFormat("M").format(it.date.time) == currentMonth.toString() } as MutableList<TransactionItem>

        return allTransactionsForMonth.filter { it.value < 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }
    }

    fun getPreviousBalanceValue(date: Calendar): Double {
        date.add(Calendar.MONTH, -1)
        val prevYear = date.get(Calendar.YEAR)
        val prevMonth = date.get(Calendar.MONTH) + 1
        date.add(Calendar.MONTH, 1)

        val allTransactionsPrevMonthForYear =
            allTransactions.value?.filter { SimpleDateFormat("yyyy").format(it.date.time) == prevYear.toString() } as MutableList<TransactionItem>
        val allTransactionsPrevMonthForMonth =
            allTransactionsPrevMonthForYear.filter { SimpleDateFormat("M").format(it.date.time) == prevMonth.toString() } as MutableList<TransactionItem>

        val prevIncome = allTransactionsPrevMonthForMonth.filter { it.value > 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }
        val prevExpense = allTransactionsPrevMonthForMonth.filter { it.value < 0 }.map { it.value }
            .fold(0.0) { acc, d -> acc + d }

        return prevIncome + prevExpense
    }

    fun getTransactions(tabSelectedReport: Int, date: Calendar): MutableList<DataItem> {
        var allTransactionsTemp = when (tabSelectedReport) {
            0 -> allTransactions.value?.toMutableList()
            1 -> allTransactions.value?.filter { it.value < 0 }
            else -> allTransactions.value?.filter { it.value > 0 }
        }
        val currentYear = date.get(Calendar.YEAR)
        val currentMonth = date.get(Calendar.MONTH) + 1
        allTransactionsTemp =
            allTransactionsTemp?.filter {
                SimpleDateFormat(
                    "yyyy",
                    Locale.US
                ).format(it.date.time) == currentYear.toString()
            }
        allTransactionsTemp =
            allTransactionsTemp?.filter {
                SimpleDateFormat(
                    "M",
                    Locale.US
                ).format(it.date.time) == currentMonth.toString()
            }
        allTransactionsTemp?.sortedByDescending { it.date }

        val groupTransactions =
            allTransactionsTemp?.groupBy {
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.US
                ).format(it.date.time)
            }
        val items = mutableListOf<DataItem>()
        if (groupTransactions != null) {
            for (date in groupTransactions) {
                val expense =
                    date.value.filter { it.value < 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                val income =
                    date.value.filter { it.value > 0 }.map { it.value }
                        .fold(0.0) { acc, d -> acc + d }
                items += DataItem.InfoDayHeaderItem(InfoDay(date.key, expense, income))
                items += date.value.map { DataItem.InfoTransactionItem(it) }
            }
        }
        return items
    }
}


class ReportViewModelFactory(private val transactionStore: TransactionStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}