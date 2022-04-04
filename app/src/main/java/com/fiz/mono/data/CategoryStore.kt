package com.fiz.mono.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE

class CategoryStore(
    private val categoryDao: CategoryDao,
    private val editString: String,
    private val addMoreString: String
) {
    var allCategoryExpense: LiveData<List<CategoryItem>> =
        categoryDao.getAllExpense().asLiveData()
    var allCategoryIncome: LiveData<List<CategoryItem>> =
        categoryDao.getAllIncome().asLiveData()

    fun getAllCategoryExpenseForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", addMoreString, ""))
            result
        }
    }

    fun getAllCategoryIncomeForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", addMoreString, ""))
            result
        }
    }

    fun getAllCategoryExpenseForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", editString, ""))
            result
        }
    }

    fun getAllCategoryIncomeForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", editString, ""))
            result
        }
    }

    fun cleanSelected() {
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
    }

    private suspend fun removeCategoryExpense(position: Int) {
        allCategoryExpense.value?.get(position)?.let {
            categoryDao.delete(it)
        }
    }

    private suspend fun removeCategoryIncome(position: Int) {
        allCategoryIncome.value?.get(position)?.let {
            categoryDao.delete(it)
        }
    }

    suspend fun deleteAll(context: Context) {
        allCategoryExpense.value?.map {
            categoryDao.delete(it)
        }
        allCategoryIncome.value?.map {
            categoryDao.delete(it)
        }

        val allCategoryExpenseDefault = mutableListOf(
            CategoryItem("e0", context.getString(R.string.bank), "bank"),
            CategoryItem("e1", context.getString(R.string.food), "food"),
            CategoryItem(
                "e2",
                context.getString(R.string.medican),
                "medican"
            ),
            CategoryItem("e3", context.getString(R.string.gym), "gym"),
            CategoryItem(
                "e4",
                context.getString(R.string.coffee),
                "coffee"
            ),
            CategoryItem(
                "e5",
                context.getString(R.string.shopping),
                "market"
            ),
            CategoryItem("e6", context.getString(R.string.cats), "cat"),
            CategoryItem("e7", context.getString(R.string.party), "party"),
            CategoryItem("e8", context.getString(R.string.gift), "gift"),
            CategoryItem("e9", context.getString(R.string.gas), "gas"),
        )
        val allCategoryIncomeDefault = mutableListOf(
            CategoryItem(
                "i0",
                context.getString(R.string.freelance),
                "challenge"
            ),
            CategoryItem("i1", context.getString(R.string.salary), "money"),
            CategoryItem("i2", context.getString(R.string.bonus), "coin"),
            CategoryItem("i3", context.getString(R.string.loan), "user"),
        )
        allCategoryExpenseDefault.forEach {
            categoryDao.insert(it)
        }
        allCategoryIncomeDefault.forEach {
            categoryDao.insert(it)
        }
    }

    suspend fun addNewCategory(name: String, type: String, selectedIcon: String) {
        if (type == TYPE_EXPENSE) {
            insertNewCategoryExpense(name, selectedIcon)
        } else {
            insertNewCategoryIncome(name, selectedIcon)
        }
    }

    private suspend fun insertNewCategoryExpense(name: String, iconID: String) {
        val numberLastItem = allCategoryExpense.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("e$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    private suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        val numberLastItem = allCategoryIncome.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("i$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    fun selectExpense(position: Int) {
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

    fun isClickAddPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.value?.size ?: false
    }

    fun isClickAddPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.value?.size ?: false
    }

    fun addSelectItemIncomeForEdit(position: Int) {
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

    fun isSelect(): Boolean {
        return allCategoryExpense.value?.any { it.selected } == true || allCategoryIncome.value?.any { it.selected } == true
    }

    suspend fun remove() {
        allCategoryExpense.value?.indexOfFirst { it.selected }.let {
            if (it == -1) return@let
            if (it != null) {
                removeCategoryExpense(it)
            }
        }

        allCategoryIncome.value?.indexOfFirst { it.selected }.let {
            if (it == -1) return@let
            if (it != null) {
                removeCategoryIncome(it)
            }
        }
    }
}