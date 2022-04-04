package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.TransactionStore

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