package com.fiz.mono.ui.category_edit

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore

class CategoryEditViewModel : ViewModel() {
    private var allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
    private var allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()

    init {
        cleanSelected()
    }

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.map { it.copy() }
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.map { it.copy() }
    }

    fun addSelectItemExpense(position: Int) {
        allCategoryIncome.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryExpense[position].selected) {
            allCategoryExpense.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryExpense[position].selected = !allCategoryExpense[position].selected
    }

    fun getVisibilityRemoveButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    private fun isSelected(): Boolean {
        return allCategoryExpense.any { it.selected } || allCategoryIncome.any { it.selected }
    }

    fun addSelectItemIncome(position: Int) {
        allCategoryExpense.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryIncome[position].selected) {
            allCategoryIncome.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryIncome[position].selected = !allCategoryIncome[position].selected
    }

    fun removeSelectItem() {
        allCategoryExpense.indexOfFirst { it.selected }.let {
            if (it == -1) return@let
            CategoryStore.removeCategoryExpense(it)
        }

        allCategoryIncome.indexOfFirst { it.selected }.let {
            if (it == -1) return@let
            CategoryStore.removeCategoryIncome(it)
        }

        allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
        allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()
    }

    fun insertNewCategory(type: String, name: String, icon: Int) {
        if (type == "") return
        if (type == CategoryEditFragment.TYPE_EXPENSE) {
            CategoryStore.insertNewCategoryExpense(name, icon)
        } else {
            CategoryStore.insertNewCategoryIncome(name, icon)
        }
        allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
        allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()
    }

    fun isClickAddPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.size - 1
    }

    fun isClickAddPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.size - 1
    }

    fun cleanSelected() {
        allCategoryExpense.forEach { it.selected = false }
        allCategoryIncome.forEach { it.selected = false }
    }


}