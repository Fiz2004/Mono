package com.fiz.mono.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.util.*

class CalendarViewModel(private val transactionStore: TransactionStore) : ViewModel() {
    private val _allTransactions: MutableLiveData<List<TransactionItem>> =
        transactionStore.allTransactions as MutableLiveData
    val allTransactions: LiveData<List<TransactionItem>> = _allTransactions

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

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

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun changeData() {
        _allTransactions.value = allTransactions.value
    }
}