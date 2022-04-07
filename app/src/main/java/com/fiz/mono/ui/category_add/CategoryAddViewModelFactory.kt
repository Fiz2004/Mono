package com.fiz.mono.ui.category_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource

class CategoryAddViewModelFactory(
    private val categoryDataSource: CategoryDataSource,
    private val categoryIconUiStateDataSource: CategoryIconUiStateDataSource,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryAddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryAddViewModel(categoryDataSource, categoryIconUiStateDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}