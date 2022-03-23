package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import com.fiz.mono.util.TimeUtils.getNumberFirstDayOfWeek
import com.fiz.mono.util.TimeUtils.getNumberLastDayOfWeek
import java.util.*

class CalendarViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModel() {
    var allTransaction = transactionStore.getAllTransactionsForInput()

    fun getListCalendarDataItem(date: Calendar): List<CalendarDataItem> {
        val transactionsForDaysCurrentMonth = getTransactionsForDaysCurrentMonth(date)
        return CalendarDataItem.getListDayWeekItem() +
                CalendarDataItem.getListDayItem(transactionsForDaysCurrentMonth)
    }

    private fun getTransactionsForDaysCurrentMonth(date: Calendar): List<TransactionsDay> {
        val result = emptyList<TransactionsDay>().toMutableList()
        result.addAll(getEmptyTransactionDayBeforeCurrentMonth(date))
        val transactionDayForCurrentMonthByDays =
            transactionStore.getTransactionDayForCurrentMonthByDays(date)
        result.addAll(transactionDayForCurrentMonthByDays)
        result.addAll(getEmptyTransactionDayAfterCurrentMonth(date))
        return result
    }

    private fun getEmptyTransactionDayBeforeCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val numberFirstDayOfWeek = getNumberFirstDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(numberFirstDayOfWeek - 1)
    }

    private fun getEmptyTransactionDayAfterCurrentMonth(
        date: Calendar
    ): MutableList<TransactionsDay> {
        val dayOfWeekLastDay = getNumberLastDayOfWeek(date)
        return TransactionsDay.getListEmptyTransactionDay(7 - dayOfWeekLastDay)
    }

    fun getListTransactionsDataItem(date: Calendar): MutableList<TransactionsDataItem> {
        val allTransactionsForDay = transactionStore.getAllTransactionsForDay(date)
        val items = mutableListOf<TransactionsDataItem>()
        items += allTransactionsForDay?.map {
            TransactionsDataItem.InfoTransactionItem(
                it
            )
        } ?: listOf()
        return items
    }
}


class CalendarViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(categoryStore, transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}