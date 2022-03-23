package com.fiz.mono.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.TransactionStore

class ReportViewModelFactory(private val transactionStore: TransactionStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}