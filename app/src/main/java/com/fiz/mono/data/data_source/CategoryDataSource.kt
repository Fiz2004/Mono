package com.fiz.mono.data.data_source

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.entity.CategoryEntity
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class CategoryDataSource(
    private val categoryDao: CategoryDao
) {
    val scope = CoroutineScope(Dispatchers.Default)

    var allCategoryExpense: Flow<List<CategoryUiState>> =
        categoryDao.getAllExpense().distinctUntilChanged()
            .map { it.map { CategoryUiState.fromCategoryEntity(it) } }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )

    var allCategoryIncome: Flow<List<CategoryUiState>> =
        categoryDao.getAllIncome().distinctUntilChanged()
            .map { it.map { CategoryUiState.fromCategoryEntity(it) } }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )

    suspend fun deleteAll(context: Context) {
        categoryDao.deleteAll()

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

        categoryDao.insertAll(allCategoryExpenseDefault)
        categoryDao.insertAll(allCategoryIncomeDefault)
    }

    suspend fun delete(categoryUiState: CategoryUiState) {
        categoryDao.delete(categoryUiState.toCategoryEntity())
    }

    suspend fun insert(newCategoryItem: CategoryEntity) {
        categoryDao.insert(newCategoryItem)
    }

}