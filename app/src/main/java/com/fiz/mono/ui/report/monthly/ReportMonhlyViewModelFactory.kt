package com.fiz.mono.ui.report.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.data_source.TransactionDataSource

class ReportMonhlyViewModelFactory(private val transactionDataSource: TransactionDataSource) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportMonthlyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportMonthlyViewModel(transactionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}