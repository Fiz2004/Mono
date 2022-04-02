package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.util.*

class CalendarViewModel(private val transactionStore: TransactionStore) : ViewModel() {
    val allTransactions = transactionStore.allTransactions

    fun getListCalendarDataItem(date: Calendar?): List<CalendarDataItem> {
        if (date == null) return listOf()
        val transactionsForDaysCurrentMonth =
            transactionStore.getTransactionsForDaysCurrentMonth(date)
        return CalendarDataItem.getListCalendarDataItem(transactionsForDaysCurrentMonth)
    }

    fun getListTransactionsDataItem(date: Calendar?): List<TransactionsDataItem> {
        if (date == null) return listOf()
        val allTransactionsForDay = transactionStore.getAllTransactionsForDay(date)
        return TransactionsDataItem.getListTransactionsDataItem(allTransactionsForDay)
    }
}

class CalendarViewModelFactory(
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}