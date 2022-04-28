package com.fiz.mono.report.ui.monthly

import com.fiz.mono.base.android.adapters.InfoDay
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.report.ui.category.TransactionsDataItem
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetTransactionsForMonth @Inject constructor() {

    operator fun invoke(
        allTransactions: List<Transaction>,
        date: LocalDate,
        tabSelectedReport: Int
    ): List<TransactionsDataItem> {
        return transactionsForAdapter(allTransactions, date, tabSelectedReport)
    }

    private val dateFormatMonthDayYear = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    fun transactionsForAdapter(
        allTransactions: List<Transaction>,
        date: LocalDate,
        tabSelectedReport: Int
    ): List<TransactionsDataItem> {
        var groupTransactions =
            getGroupTransactionsByDays(allTransactions, date)

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

    private fun getGroupTransactionsByDays(allTransactions: List<Transaction>, date: LocalDate) =
        getAllTransactionsForMonth(allTransactions, date).groupBy {
            dateFormatMonthDayYear.format(it.localDate)
        }


    private fun getAllTransactionsForMonth(
        allTransactions: List<Transaction>,
        date: LocalDate
    ): List<Transaction> {
        return allTransactions.filter { it.localDate.year == date.year }
            .filter { it.localDate.month == date.month }
    }
}