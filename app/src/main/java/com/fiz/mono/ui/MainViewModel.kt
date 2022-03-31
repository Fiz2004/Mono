package com.fiz.mono.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.calendar.CalendarDataItem
import com.fiz.mono.ui.category_edit.CategoryEditFragment
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) : ViewModel() {
    var allCategoryExpense = categoryStore.getAllCategoryExpenseForInput()
    var allCategoryIncome = categoryStore.getAllCategoryIncomeForInput()

    var allTransaction = transactionStore.getAllTransactionsForInput()

    private var _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    fun getListCalendarDataItem(date: Calendar? = _date.value): List<CalendarDataItem> {
        if (date == null) return listOf()
        val transactionsForDaysCurrentMonth =
            transactionStore.getTransactionsForDaysCurrentMonth(date)
        return CalendarDataItem.getListCalendarDataItem(transactionsForDaysCurrentMonth)
    }

    fun getListTransactionsDataItem(date: Calendar? = _date.value): List<TransactionsDataItem> {
        if (date == null) return listOf()
        val allTransactionsForDay = transactionStore.getAllTransactionsForDay(date)
        return TransactionsDataItem.getListTransactionsDataItem(allTransactionsForDay)
    }

    fun getFormatDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date.value?.time ?: "")
    }

    fun addNewCategory(name: String, type: String, selectedIcon: String) {
        viewModelScope.launch {
            if (type == CategoryEditFragment.TYPE_EXPENSE) {
                categoryStore.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryStore.insertNewCategoryIncome(name, selectedIcon)
            }
        }
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

    fun isSelected(): Boolean {
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


    fun setMonth(month: Int) {
        _date.value?.set(Calendar.MONTH, month)
        _date.value = date.value
    }

    fun setDate(day: Int) {
        _date.value?.set(Calendar.DATE, day)
        _date.value = date.value
    }

    fun dateDayPlusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, 1)
        _date.value = date.value
    }

    fun dateDayMinusOne() {
        _date.value?.add(Calendar.DAY_OF_YEAR, -1)
        _date.value = date.value
    }

    fun dateMonthPlusOne() {
        _date.value?.add(Calendar.MONTH, 1)
        _date.value = date.value
    }

    fun dateMonthMinusOne() {
        _date.value?.add(Calendar.MONTH, -1)
        _date.value = date.value
    }
}