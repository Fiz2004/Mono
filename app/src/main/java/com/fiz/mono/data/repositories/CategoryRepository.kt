package com.fiz.mono.data.repositories

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.entity.CategoryEntity
import com.fiz.mono.ui.models.CategoryUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDataSource: CategoryDataSource,
    private val editString: String,
    private val addMoreString: String
) {
    var allCategoryExpense: Flow<List<CategoryUiState>> =
        categoryDataSource.allCategoryExpense
    var allCategoryIncome: Flow<List<CategoryUiState>> =
        categoryDataSource.allCategoryIncome

    fun getAllCategoryExpenseForEdit(): Flow<List<CategoryUiState>> {
        return categoryDataSource.allCategoryExpense.map {
            it + CategoryUiState(
                "e",
                addMoreString,
                0
            )
        }
    }

    fun getAllCategoryIncomeForEdit(): Flow<List<CategoryUiState>> {
        return categoryDataSource.allCategoryIncome.map {
            it + CategoryUiState(
                "i",
                addMoreString,
                0
            )
        }
    }

    fun getAllCategoryExpenseForInput(): Flow<List<CategoryUiState>> {
        return categoryDataSource.allCategoryExpense.map {
            it + CategoryUiState(
                "e",
                editString,
                0
            )
        }
    }

    fun getAllCategoryIncomeForInput(): Flow<List<CategoryUiState>> {
        return categoryDataSource.allCategoryIncome.map { it + CategoryUiState("i", editString, 0) }
    }

    suspend fun removeCategoryExpense(categoryUiState: CategoryUiState) {
        categoryDataSource.delete(categoryUiState)
    }

    suspend fun removeCategoryIncome(categoryUiState: CategoryUiState) {
        categoryDataSource.delete(categoryUiState)
    }

    suspend fun deleteAll(context: Context) {
        categoryDataSource.deleteAll(context)

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

        categoryDataSource.insertAll(allCategoryExpenseDefault)
        categoryDataSource.insertAll(allCategoryIncomeDefault)
    }

    suspend fun insertNewCategoryExpense(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("e$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("i$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }
}