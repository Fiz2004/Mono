package com.fiz.mono.core.domain.repositories

import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    var allCategoryExpense: Flow<List<Category>>
    var allCategoryIncome: Flow<List<Category>>

    fun getAllCategoryExpenseForInput(): Flow<Resource<List<Category>>>

    fun getAllCategoryIncomeForInput(): Flow<List<Category>>

    suspend fun removeCategory(category: Category)

    suspend fun insertNewCategoryExpense(name: String, iconID: String)

    suspend fun insertNewCategoryIncome(name: String, iconID: String)
}