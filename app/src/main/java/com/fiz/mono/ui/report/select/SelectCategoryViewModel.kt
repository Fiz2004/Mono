package com.fiz.mono.ui.report.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore

class SelectCategoryViewModel(private val categoryStore: CategoryStore) : ViewModel() {
    var allCategoryExpense = categoryStore.allCategoryExpense
    var allCategoryIncome = categoryStore.allCategoryIncome
}

class SelectCategoryViewModelFactory(private val categoryStore: CategoryStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelectCategoryViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}