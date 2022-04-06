package com.fiz.mono.ui.category_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.data_source.CategoryDataSource

class CategoryEditViewModelFactory(
    private val categoryDataSource: CategoryDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryEditViewModel(categoryDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}