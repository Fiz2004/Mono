package com.fiz.mono.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.TransactionDataSource

class InputViewModelFactory(
    private val categoryDataSource: CategoryDataSource,
    private val transactionDataSource: TransactionDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputViewModel(categoryDataSource, transactionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}