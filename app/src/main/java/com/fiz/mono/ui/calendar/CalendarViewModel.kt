package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore

class CalendarViewModel(private val categoryStore: CategoryStore) : ViewModel()

class CalendarViewModelFactory(private val categoryStore: CategoryStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}