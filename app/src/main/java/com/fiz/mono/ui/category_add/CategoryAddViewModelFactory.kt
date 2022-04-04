package com.fiz.mono.ui.category_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore

class CategoryAddViewModelFactory(
    private val categoryStore: CategoryStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryAddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryAddViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}