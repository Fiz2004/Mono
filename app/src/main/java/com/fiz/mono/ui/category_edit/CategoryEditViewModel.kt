package com.fiz.mono.ui.category_edit

import androidx.lifecycle.*
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.ui.input.InputFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CategoryEditViewModel(
    private val categoryStore: CategoryStore
) : ViewModel() {
    var allCategoryExpenseForEdit = categoryStore.getAllCategoryExpenseForEdit()
    var allCategoryIncomeForEdit = categoryStore.getAllCategoryIncomeForEdit()

    private var _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    fun getAllCategoryFromSelectedForEdit(selectedAdapter: Int?): List<CategoryItem> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            getCopyAllCategoryItemExpenseForEdit()
        else
            getCopyAllCategoryItemIncomeForEdit()
    }

    fun getFormatDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date.value?.time ?: "")
    }

    fun getCopyAllCategoryItemExpenseForEdit(): List<CategoryItem> {
        return allCategoryExpenseForEdit.value?.map { it.copy() } ?: listOf()
    }

    fun getCopyAllCategoryItemIncomeForEdit(): List<CategoryItem> {
        return allCategoryIncomeForEdit.value?.map { it.copy() } ?: listOf()
    }

    fun addSelectItemExpenseForEdit(position: Int) {
        allCategoryIncomeForEdit.value?.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryExpenseForEdit.value?.get(position)?.selected!!) {
            allCategoryExpenseForEdit.value?.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryExpenseForEdit.value?.get(position)?.selected =
            !allCategoryExpenseForEdit.value?.get(position)?.selected!!
    }

    fun isSelectedForEdit(): Boolean {
        return allCategoryExpenseForEdit.value?.any { it.selected } == true || allCategoryIncomeForEdit.value?.any { it.selected } == true
    }

    fun addSelectItemIncomeForEdit(position: Int) {
        allCategoryExpenseForEdit.value?.find { it.selected }?.let {
            it.selected = false
        }

        if (!allCategoryIncomeForEdit.value?.get(position)?.selected!!) {
            allCategoryIncomeForEdit.value?.find { it.selected }?.let {
                it.selected = false
            }
        }
        allCategoryIncomeForEdit.value?.get(position)?.selected =
            !allCategoryIncomeForEdit.value?.get(position)?.selected!!
    }

    fun removeSelectItem() {
        viewModelScope.launch {
            allCategoryExpenseForEdit.value?.indexOfFirst { it.selected }.let {
                if (it == -1) return@let
                if (it != null) {
                    categoryStore.removeCategoryExpense(it)
                }
            }

            allCategoryIncomeForEdit.value?.indexOfFirst { it.selected }.let {
                if (it == -1) return@let
                if (it != null) {
                    categoryStore.removeCategoryIncome(it)
                }
            }
        }
    }

    fun isClickAddPositionExpense(position: Int): Boolean {
        return position == allCategoryExpenseForEdit.value?.size?.minus(1) ?: false
    }

    fun isClickAddPositionIncome(position: Int): Boolean {
        return position == allCategoryIncomeForEdit.value?.size?.minus(1) ?: false
    }

    fun cleanSelectedForEdit() {
        allCategoryExpenseForEdit.value?.forEach { it.selected = false }
        allCategoryIncomeForEdit.value?.forEach { it.selected = false }
    }
}

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