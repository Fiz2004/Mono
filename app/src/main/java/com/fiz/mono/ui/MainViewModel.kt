package com.fiz.mono.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.ui.calendar.CalendarDataItem
import com.fiz.mono.ui.category_edit.CategoryEditFragment
import com.fiz.mono.ui.input.InputFragment
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) : ViewModel() {
    var allCategoryExpenseForEdit = categoryStore.getAllCategoryExpenseForEdit()
    var allCategoryIncomeForEdit = categoryStore.getAllCategoryIncomeForEdit()

    var allCategoryExpenseForInput = categoryStore.getAllCategoryExpenseForInput()
    var allCategoryIncomeForInput = categoryStore.getAllCategoryIncomeForInput()

    var allTransaction = transactionStore.allTransactions

    private var _date = MutableLiveData(Calendar.getInstance())
    val date: LiveData<Calendar> = _date

    fun setSelected(selectedAdapter: Int?, nameCategory: String) {
        addSelectItem(
            getAllCategoryFromSelectedForInput(selectedAdapter).indexOfFirst { it.name == nameCategory },
            selectedAdapter
        )
    }

    fun getAllCategoryFromSelectedForEdit(selectedAdapter: Int?): List<CategoryItem> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            getCopyAllCategoryItemExpenseForEdit()
        else
            getCopyAllCategoryItemIncomeForEdit()
    }

    fun getAllCategoryFromSelectedForInput(selectedAdapter: Int?): List<CategoryItem> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            getCopyAllCategoryItemExpenseForInput()
        else
            getCopyAllCategoryItemIncomeForInput()
    }

    fun isClickEditPosition(position: Int, selectedAdapter: Int?): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == allCategoryExpenseForInput.value?.size?.minus(1) ?: 0
        else
            position == allCategoryIncomeForInput.value?.size?.minus(1) ?: 0
    }

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

    fun getCopyAllCategoryItemExpenseForEdit(): List<CategoryItem> {
        return allCategoryExpenseForEdit.value?.map { it.copy() } ?: listOf()
    }

    fun getCopyAllCategoryItemIncomeForEdit(): List<CategoryItem> {
        return allCategoryIncomeForEdit.value?.map { it.copy() } ?: listOf()
    }

    fun getCopyAllCategoryItemExpenseForInput(): List<CategoryItem> {
        return allCategoryExpenseForInput.value?.map { it.copy() } ?: listOf()
    }

    fun getCopyAllCategoryItemIncomeForInput(): List<CategoryItem> {
        return allCategoryIncomeForInput.value?.map { it.copy() } ?: listOf()
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

    fun isSelectedForInput(selectedAdapter: Int?): Boolean {
        return getAllCategoryFromSelectedForInput(selectedAdapter).firstOrNull { it.selected } != null
    }

    fun removeTransaction(transaction: TransactionItem?) {
        viewModelScope.launch {
            transaction?.let { transactionStore.delete(it) }
        }
    }

    fun findTransaction(currentTransaction: Int): TransactionItem? {
        return if (currentTransaction != -1)
            transactionStore.allTransactions.value?.find { it.id == currentTransaction }?.copy()
        else
            null
    }

    private fun addSelectItemExpenseForInput(position: Int) {
        if (!allCategoryExpenseForInput.value?.get(position)?.selected!!) {
            allCategoryExpenseForInput.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpenseForInput.value?.get(position)!!.selected =
            !allCategoryExpenseForInput.value?.get(position)!!.selected
    }

    private fun addSelectItemIncomeForInput(position: Int) {
        if (!allCategoryIncomeForInput.value?.get(position)?.selected!!) {
            allCategoryIncomeForInput.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncomeForInput.value?.get(position)?.selected =
            !allCategoryIncomeForInput.value?.get(position)?.selected!!
    }

    fun addSelectItem(position: Int, selectedAdapter: Int?) {
        if (selectedAdapter == InputFragment.EXPENSE)
            addSelectItemExpenseForInput(position)
        else
            addSelectItemIncomeForInput(position)
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

    fun clickUpdate(transaction: TransactionItem) {
        viewModelScope.launch {
            transactionStore.updateTransaction(transaction)
        }
    }

    fun getSelectedInputForInput(selectedAdapter: Int?): CategoryItem {
        return getAllCategoryFromSelectedForInput(selectedAdapter).first { it.selected }
    }

    fun clickSubmit(
        transaction: TransactionItem
    ) {
        viewModelScope.launch {
            transactionStore.insertNewTransaction(transaction)
        }
    }

    fun getNewId(): Int {
        val lastItem = allTransaction.value?.lastOrNull()
        val id = lastItem?.id
        return id?.let { it + 1 } ?: 0
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

    fun cleanSelectedForInput() {
        allCategoryExpenseForInput.value?.forEach { it.selected = false }
        allCategoryIncomeForInput.value?.forEach { it.selected = false }
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