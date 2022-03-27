package com.fiz.mono.ui.category_edit

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import kotlinx.coroutines.launch

class CategoryEditViewModel(private val categoryStore: CategoryStore) : ViewModel() {
    var allCategoryExpense = categoryStore.getAllCategoryExpenseForEdit()
    var allCategoryIncome = categoryStore.getAllCategoryIncomeForEdit()

    init {
        cleanSelected()
    }

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.value?.map { it.copy() } ?: listOf()
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.value?.map { it.copy() } ?: listOf()
    }

    fun addSelectItemExpense(position: Int) {
        allCategoryIncome.value?.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryExpense.value?.get(position)?.selected!!) {
            allCategoryExpense.value?.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryExpense.value?.get(position)?.selected =
            !allCategoryExpense.value?.get(position)?.selected!!
    }

    fun getVisibilityRemoveButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    private fun isSelected(): Boolean {
        return allCategoryExpense.value?.any { it.selected } == true || allCategoryIncome.value?.any { it.selected } == true
    }

    fun addSelectItemIncome(position: Int) {
        allCategoryExpense.value?.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryIncome.value?.get(position)?.selected!!) {
            allCategoryIncome.value?.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryIncome.value?.get(position)?.selected =
            !allCategoryIncome.value?.get(position)?.selected!!
    }

    fun removeSelectItem() {
        viewModelScope.launch {
            allCategoryExpense.value?.indexOfFirst { it.selected }.let {
                if (it == -1) return@let
                if (it != null) {
                    categoryStore.removeCategoryExpense(it)
                }
            }

            allCategoryIncome.value?.indexOfFirst { it.selected }.let {
                if (it == -1) return@let
                if (it != null) {
                    categoryStore.removeCategoryIncome(it)
                }
            }
        }
    }

    fun isClickAddPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.value?.size?.minus(1) ?: false
    }

    fun isClickAddPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.value?.size?.minus(1) ?: false
    }

    fun cleanSelected() {
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
    }
}

class CategoryEditViewModelFactory(private val categoryStore: CategoryStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryEditViewModel(categoryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}