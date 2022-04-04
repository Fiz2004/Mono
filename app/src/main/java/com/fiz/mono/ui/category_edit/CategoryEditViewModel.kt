package com.fiz.mono.ui.category_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import kotlinx.coroutines.launch

class CategoryEditViewModel(
    private val categoryStore: CategoryStore
) : ViewModel() {
    private var _allCategoryExpense: MutableLiveData<List<CategoryItem>> =
        categoryStore.getAllCategoryExpenseForEdit() as MutableLiveData
    val allCategoryExpense: LiveData<List<CategoryItem>> = _allCategoryExpense

    private var _allCategoryIncome: MutableLiveData<List<CategoryItem>> =
        categoryStore.getAllCategoryIncomeForEdit() as MutableLiveData
    val allCategoryIncome: LiveData<List<CategoryItem>> = _allCategoryIncome

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    private val _isMoveAdd = MutableLiveData(false)
    val isMoveAdd: LiveData<Boolean> = _isMoveAdd

    var type: String = TYPE_EXPENSE

    fun expenseRecyclerView(position: Int) {
        if (categoryStore.isClickAddPositionExpense(position)) {
            categoryStore.cleanSelected()
            type = TYPE_EXPENSE
            _isMoveAdd.value = true
            return
        }

        categoryStore.selectExpense(position)
        _allCategoryExpense.value = allCategoryExpense.value
        _allCategoryIncome.value = allCategoryIncome.value
    }

    fun incomeRecyclerView(position: Int) {
        if (categoryStore.isClickAddPositionIncome(position)) {
            categoryStore.cleanSelected()
            type = TYPE_INCOME
            _isMoveAdd.value = true
            return
        }

        categoryStore.addSelectItemIncomeForEdit(position)
        _allCategoryExpense.value = allCategoryExpense.value
        _allCategoryIncome.value = allCategoryIncome.value
    }

    fun isSelect(): Boolean {
        return categoryStore.isSelect()
    }

    fun removeSelectItem() {
        viewModelScope.launch {
            categoryStore.remove()
        }
    }

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun moveAdd() {
        _isMoveAdd.value = false
    }

    fun getActionForMoveAdd(): NavDirections {
        return if (type == TYPE_EXPENSE)
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
        else
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}