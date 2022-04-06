package com.fiz.mono.data.data_source

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.data.entity.Category
import com.fiz.mono.ui.category_edit.CategoryEditViewModel.Companion.TYPE_EXPENSE
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CategoryDataSource(
    private val categoryDao: CategoryDao,
    private val editString: String,
    private val addMoreString: String
) {
    var allCategoryExpense: Flow<List<CategoryUiState>> =
        categoryDao.getAllExpense().distinctUntilChanged().map { it.map { it.toCategoryUiState() } }
    var allCategoryIncome: Flow<List<CategoryUiState>> =
        categoryDao.getAllIncome().distinctUntilChanged().map { it.map { it.toCategoryUiState() } }

    fun getAllCategoryExpenseForEdit(): Flow<List<CategoryUiState>> {
        return allCategoryExpense.map { it + CategoryUiState("e", addMoreString, 0) }
    }

    fun getAllCategoryIncomeForEdit(): Flow<List<CategoryUiState>> {
        return allCategoryExpense.map { it + CategoryUiState("i", addMoreString, 0) }
    }

    fun getAllCategoryExpenseForInput(): Flow<List<CategoryUiState>> {
        return allCategoryExpense.map { it + CategoryUiState("e", editString, 0) }
    }

    fun getAllCategoryIncomeForInput(): Flow<List<CategoryUiState>> {
        return allCategoryIncome.map { it + CategoryUiState("i", editString, 0) }
    }

    suspend fun cleanSelected() {
        allCategoryExpense.first().forEach { it.selectedFalse() }
        allCategoryIncome.first().forEach { it.selectedFalse() }
    }

    private suspend fun removeCategoryExpense(position: Int) {
        allCategoryExpense.first()[position].let {
            categoryDao.delete(it.toCategory())
        }
    }

    private suspend fun removeCategoryIncome(position: Int) {
        allCategoryIncome.first()[position].let {
            categoryDao.delete(it.toCategory())
        }
    }

    suspend fun deleteAll(context: Context) {
        allCategoryExpense.first().map {
            categoryDao.delete(it.toCategory())
        }
        allCategoryIncome.first().map {
            categoryDao.delete(it.toCategory())
        }

        val allCategoryExpenseDefault = mutableListOf(
            Category("e0", context.getString(R.string.bank), "bank"),
            Category("e1", context.getString(R.string.food), "food"),
            Category(
                "e2",
                context.getString(R.string.medican),
                "medican"
            ),
            Category("e3", context.getString(R.string.gym), "gym"),
            Category(
                "e4",
                context.getString(R.string.coffee),
                "coffee"
            ),
            Category(
                "e5",
                context.getString(R.string.shopping),
                "market"
            ),
            Category("e6", context.getString(R.string.cats), "cat"),
            Category("e7", context.getString(R.string.party), "party"),
            Category("e8", context.getString(R.string.gift), "gift"),
            Category("e9", context.getString(R.string.gas), "gas"),
        )
        val allCategoryIncomeDefault = mutableListOf(
            Category(
                "i0",
                context.getString(R.string.freelance),
                "challenge"
            ),
            Category("i1", context.getString(R.string.salary), "money"),
            Category("i2", context.getString(R.string.bonus), "coin"),
            Category("i3", context.getString(R.string.loan), "user"),
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
        val numberLastItem = allCategoryExpense.first().lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = Category("e$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    private suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        val numberLastItem = allCategoryIncome.first().lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = Category("i$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    suspend fun selectExpense(position: Int) {
        allCategoryIncome.first().find { it.selected }?.selectedFalse()

        val list = allCategoryExpense.first()

        if (!list[position].selected) {
            list.find { it.selected }?.selectedFalse()
        }

        list[position].invertSelected()
    }

    suspend fun isClickAddPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.first().size
    }

    suspend fun isClickAddPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.first().size
    }

    suspend fun selectIncome(position: Int) {
        allCategoryExpense.first().find { it.selected }?.selectedFalse()

        val list = allCategoryIncome.first()

        if (!list[position].selected) {
            list.find { it.selected }?.selectedFalse()
        }

        list[position].invertSelected()
    }

    suspend fun isSelect(): Boolean {
        return allCategoryExpense.first()
            .any { it.selected } || allCategoryIncome.first().any { it.selected }

    }

    suspend fun remove() {
        allCategoryExpense.collect {
            it.indexOfFirst { it.selected }.let {
                if (it == -1) return@let
                removeCategoryExpense(it)
            }
        }

        allCategoryIncome.first().indexOfFirst { it.selected }.let {
            if (it == -1) return@let
            removeCategoryIncome(it)
        }
    }
}