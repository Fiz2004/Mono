package com.fiz.mono.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore

class MainViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore,
    private val sharedPreferences: SharedPreferences
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(categoryStore, transactionStore, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}