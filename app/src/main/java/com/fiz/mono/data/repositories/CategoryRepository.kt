package com.fiz.mono.data.repositories

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

    suspend fun insertNewCategoryExpense(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("e$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(newId: Int, name: String, iconID: String) {
        val newCategoryItem = CategoryEntity("i$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }
}