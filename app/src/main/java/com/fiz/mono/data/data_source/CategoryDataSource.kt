package com.fiz.mono.data.data_source

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.data.entity.Category
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
        return allCategoryIncome.map { it + CategoryUiState("i", addMoreString, 0) }
    }

    fun getAllCategoryExpenseForInput(): Flow<List<CategoryUiState>> {
        return allCategoryExpense.map { it + CategoryUiState("e", editString, 0) }
    }

    fun getAllCategoryIncomeForInput(): Flow<List<CategoryUiState>> {
        return allCategoryIncome.map { it + CategoryUiState("i", editString, 0) }
    }

    suspend fun removeCategoryExpense(categoryUiState: CategoryUiState) {
        categoryDao.delete(categoryUiState.toCategory())
    }

    suspend fun removeCategoryIncome(categoryUiState: CategoryUiState) {
        categoryDao.delete(categoryUiState.toCategory())
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

    suspend fun insertNewCategoryExpense(newId: Int, name: String, iconID: String) {
        val newCategoryItem = Category("e$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(newId: Int, name: String, iconID: String) {
        val newCategoryItem = Category("i$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

}