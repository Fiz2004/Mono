package com.fiz.mono.ui.category_add

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryIcon
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.categoryIcons
import com.fiz.mono.ui.category_edit.CategoryEditFragment
import com.fiz.mono.ui.category_edit.CategoryEditViewModel
import kotlinx.coroutines.launch

class CategoryAddViewModel(private val categoryStore: CategoryStore) : ViewModel() {
    private val allCategoryIcon = categoryIcons

    init {
        allCategoryIcon.forEach { it.selected = false }
    }

    fun addSelectItem(position: Int) {
        if (!allCategoryIcon[position].selected) {
            allCategoryIcon.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIcon[position].selected = !allCategoryIcon[position].selected
    }

    fun getAllCategoryIcon(): List<CategoryIcon> {
        return allCategoryIcon.map { it.copy() }
    }

    fun getSelectedIcon(): String {
        return allCategoryIcon.first { it.selected }.id
    }

    fun getVisibilityAddButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    fun isSelected(): Boolean {
        return allCategoryIcon.any { it.selected }
    }

    fun addNewCategory(name: String, type: String) {
        viewModelScope.launch {
            if (type == CategoryEditFragment.TYPE_EXPENSE) {
                categoryStore.insertNewCategoryExpense(name, getSelectedIcon())
            } else {
                categoryStore.insertNewCategoryIncome(name, getSelectedIcon())
            }
        }
    }
}

class CategoryAddViewModelFactory(private val categoryStore: CategoryStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryAddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryAddViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}