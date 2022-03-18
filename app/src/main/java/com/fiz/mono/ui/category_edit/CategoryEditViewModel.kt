package com.fiz.mono.ui.category_edit

import android.view.View
import androidx.lifecycle.ViewModel
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore

private const val ADAPTER_EXPENSE = 0
private const val ADAPTER_INCOME = 1

class CategoryEditViewModel : ViewModel() {
    private var selectedAdapter: Int? = null
    private var selectedItem: Int? = null

    private var allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
    private var allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.map { it.copy() }
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.map { it.copy() }
    }

    fun addSelectItemExpense(position: Int) {
        if (selectedAdapter != ADAPTER_EXPENSE) {
            selectedItem?.let {
                allCategoryIncome[it].selected = false
            }
            selectedItem = null
        }

        selectedAdapter = ADAPTER_EXPENSE

        if (selectedItem == position) {
            selectedItem = null
            allCategoryExpense[position].selected = false
        } else {
            selectedItem?.let {
                allCategoryExpense[it].selected = false
            }
            selectedItem = position
            allCategoryExpense[position].selected = true
        }
    }

    fun getVisibilityRemoveButton(): Int {
        return if (isSelected())
            View.VISIBLE
        else
            View.GONE
    }

    private fun isSelected(): Boolean {
        return selectedItem != null
    }

    fun addSelectItemIncome(position: Int) {
        if (selectedAdapter != ADAPTER_INCOME) {
            selectedItem?.let {
                allCategoryExpense[it].selected = false
            }
            selectedItem = null
        }
        selectedAdapter = ADAPTER_INCOME

        if (selectedItem == position) {
            selectedItem = null
            allCategoryIncome[position].selected = false
        } else {
            selectedItem?.let {
                allCategoryIncome[it].selected = false
            }
            selectedItem = position
            allCategoryIncome[position].selected = true
        }
    }

    fun removeSelectItem() {
        if (isSelectExpense()) {
            if (selectedItem != null) {
                CategoryStore.removeCategoryExpense(selectedItem!!)
            }
            CategoryStore.getAllCategoryExpenseForEdit()[selectedItem!!].selected = false
            selectedItem = null

        } else {
            if (selectedItem != null) {
                CategoryStore.removeCategoryIncome(selectedItem!!)
            }
            CategoryStore.getAllCategoryIncomeForEdit()[selectedItem!!].selected = false
            selectedItem = null
        }
        allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
        allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()
    }

    private fun isSelectExpense(): Boolean {
        return selectedAdapter == ADAPTER_EXPENSE
    }

    fun insertNewCategory(type: String, name: String, icon: Int) {
        if (type == CategoryEditFragment.TYPE_EXPENSE) {
            CategoryStore.insertNewCategoryExpense(CategoryItem(name, icon))
        } else {
            CategoryStore.insertNewCategoryIncome(CategoryItem(name, icon))
        }
        allCategoryExpense = CategoryStore.getAllCategoryExpenseForEdit()
        allCategoryIncome = CategoryStore.getAllCategoryIncomeForEdit()
    }

    fun isClickAddPositionExpense(position: Int): Boolean {
        return position == getAllCategoryItemExpense().size - 1
    }

    fun isClickAddPositionIncome(position: Int): Boolean {
        return position == getAllCategoryItemIncome().size - 1
    }

    fun cleanSelected() {
        allCategoryExpense.forEach { it.selected = false }
        allCategoryIncome.forEach { it.selected = false }
        selectedAdapter = null
        selectedItem = null
    }


}