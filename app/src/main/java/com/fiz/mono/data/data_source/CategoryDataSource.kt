package com.fiz.mono.data.data_source

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao
import com.fiz.mono.data.entity.CategoryEntity
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
            CategoryEntity("e0", context.getString(R.string.bank), "bank"),
            CategoryEntity("e1", context.getString(R.string.food), "food"),
            CategoryEntity(
                "e2",
                context.getString(R.string.medican),
                "medican"
            ),
            CategoryEntity("e3", context.getString(R.string.gym), "gym"),
            CategoryEntity(
                "e4",
                context.getString(R.string.coffee),
                "coffee"
            ),
            CategoryEntity(
                "e5",
                context.getString(R.string.shopping),
                "market"
            ),
            CategoryEntity("e6", context.getString(R.string.cats), "cat"),
            CategoryEntity("e7", context.getString(R.string.party), "party"),
            CategoryEntity("e8", context.getString(R.string.gift), "gift"),
            CategoryEntity("e9", context.getString(R.string.gas), "gas"),
        )
        val allCategoryIncomeDefault = mutableListOf(
            CategoryEntity(
                "i0",
                context.getString(R.string.freelance),
                "challenge"
            ),
            CategoryEntity("i1", context.getString(R.string.salary), "money"),
            CategoryEntity("i2", context.getString(R.string.bonus), "coin"),
            CategoryEntity("i3", context.getString(R.string.loan), "user"),
        )
        allCategoryExpenseDefault.forEach {
            categoryDao.insert(it)
        }
        allCategoryIncomeDefault.forEach {
            categoryDao.insert(it)
        }
    }

    suspend fun insertNewCategoryExpense(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("e$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("i$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

}