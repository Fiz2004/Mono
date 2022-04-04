package com.fiz.mono.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore

class InputViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputViewModel(categoryStore, transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}