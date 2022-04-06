package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.data_source.TransactionDataSource

class CalendarViewModelFactory(
    private val transactionDataSource: TransactionDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(transactionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}