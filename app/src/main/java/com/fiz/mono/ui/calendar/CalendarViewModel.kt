package com.fiz.mono.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import java.util.*

class CalendarViewModel(private val transactionDataSource: TransactionDataSource) : ViewModel() {
    private val _allTransactions: MutableLiveData<List<Transaction>> =
        transactionDataSource.allTransactions as MutableLiveData
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    fun getListCalendarDataItem(date: Calendar?): List<CalendarDataItem> {
        if (date == null) return listOf()
        val transactionsForDaysCurrentMonth =
            transactionDataSource.getTransactionsForDaysCurrentMonth(date)
        return CalendarDataItem.getListCalendarDataItem(transactionsForDaysCurrentMonth)
    }

    fun getListTransactionsDataItem(date: Calendar?): List<TransactionsDataItem> {
        if (date == null) return listOf()
        val allTransactionsForDay = transactionDataSource.getAllTransactionsForDay(date)
        return TransactionsDataItem.getListTransactionsDataItem(allTransactionsForDay)
    }

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun changeData() {
        _allTransactions.value = allTransactions.value
    }
}