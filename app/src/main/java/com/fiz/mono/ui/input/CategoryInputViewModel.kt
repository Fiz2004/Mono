package com.fiz.mono.ui.input

import android.content.Context
import android.net.Uri
import android.widget.Button
import androidx.lifecycle.ViewModel
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import java.util.*

class CategoryInputViewModel : ViewModel() {
    private var allCategoryExpense = CategoryStore.getAllCategoryExpenseForInput()
    private var allCategoryIncome = CategoryStore.getAllCategoryIncomeForInput()

    private var selectedAdapter: Int = InputFragment.EXPENSE

    private var load = false
    lateinit var loadData: List<Uri>

    var state = ""

    fun setData(results: List<Uri>) {
        load = true
        loadData = results
    }

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.map { it.copy() }
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.map { it.copy() }
    }

    fun setSelectedAdapter(adapter: Int) {
        selectedAdapter = adapter
        if (adapter == InputFragment.EXPENSE)
            allCategoryIncome.forEach { it.selected = false }
        else
            allCategoryExpense.forEach { it.selected = false }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (selectedAdapter) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            InputFragment.INCOME -> context.getString(R.string.income)
            else -> {
                throw Error("Not adapter")
            }
        }
    }

    fun clickSubmit(absValue: Double, note: String) {
        val selectedCategoryItem = getAllCategoryFromSelected().first { it.selected }

        val value = if (selectedAdapter == InputFragment.EXPENSE)
            -absValue
        else
            absValue

        TransactionStore.insertNewTransaction(
            TransactionItem(
                Calendar.getInstance().time,
                value,
                selectedCategoryItem.name,
                note,
                selectedCategoryItem.imgSrc
            )
        )
        (if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense
        else
            allCategoryIncome).first { it.selected }.selected = false

        state = "Только что отправили"
    }

    fun getAllCategoryFromSelected(): List<CategoryItem> {
        return (if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense
        else
            allCategoryIncome)
    }

    fun getSelectedAdapter(): Int {
        return selectedAdapter
    }

    fun isClickEditPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.size - 1
    }

    fun isClickEditPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.size - 1
    }

    fun cleanSelected() {
        allCategoryExpense.forEach { it.selected = false }
        allCategoryIncome.forEach { it.selected = false }
    }

    fun setStateSubmitInputButton(submitButton: Button) {
        if (getAllCategoryFromSelected().firstOrNull { it.selected } == null)
            submitButton.setDisabled()
        else
            submitButton.setEnabled()
    }

    fun addSelectItem(position: Int) {
        if (getSelectedAdapter() == InputFragment.EXPENSE)
            addSelectItemExpense(position)
        else
            addSelectItemIncome(position)
    }


    private fun addSelectItemExpense(position: Int) {
        if (!allCategoryExpense[position].selected) {
            allCategoryExpense.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpense[position].selected = !allCategoryExpense[position].selected
    }

    private fun addSelectItemIncome(position: Int) {
        if (!allCategoryIncome[position].selected) {
            allCategoryIncome.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncome[position].selected = !allCategoryIncome[position].selected
    }

    fun getListForSubmitAdapter(): List<CategoryItem> {
        return if (getSelectedAdapter() == InputFragment.EXPENSE)
            getAllCategoryItemExpense()
        else
            getAllCategoryItemIncome()

    }

}