package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    var observeCategoriesExpense: Flow<List<Category>>
    var observeCategoriesIncome: Flow<List<Category>>

    suspend fun removeCategory(category: Category)

    suspend fun insertNewCategoryExpense(name: String, iconID: String)

    suspend fun insertNewCategoryIncome(name: String, iconID: String)
    suspend fun initDefaultValue()
}