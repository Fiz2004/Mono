package com.fiz.mono.ui.category_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.CategoryStore

class CategoryEditViewModelFactory(
    private val categoryStore: CategoryStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryEditViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}