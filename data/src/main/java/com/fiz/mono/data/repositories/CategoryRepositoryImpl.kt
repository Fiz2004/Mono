package com.fiz.mono.data.repositories

import com.fiz.mono.data.data_source.CategoryLocalDataSource
import com.fiz.mono.data.entity.CategoryEntity
import com.fiz.mono.data.mapper.toCategory
import com.fiz.mono.data.mapper.toCategoryEntity
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.repositories.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : CategoryRepository {
    override var observeCategoriesExpense: Flow<List<Category>> =
        categoryLocalDataSource.observeCategoriesExpense
            .map {
                it.map { it.toCategory() }
            }.flowOn(defaultDispatcher)

    override var observeCategoriesIncome: Flow<List<Category>> =
        categoryLocalDataSource.observeCategoriesIncome
            .map {
                it.map { it.toCategory() }
            }.flowOn(defaultDispatcher)

    override suspend fun removeCategory(category: Category) =
        withContext(defaultDispatcher) {
            categoryLocalDataSource.delete(category.toCategoryEntity())
        }

    override suspend fun insertNewCategoryExpense(name: String, iconID: String) {
        withContext(defaultDispatcher) {
            val categoriesExpense: List<Category> =
                categoryLocalDataSource.getCategoriesExpense().map { it.toCategory() }
            val numberLastItem = categoriesExpense.lastOrNull()?.id?.substring(1)?.toInt()
            val newId = numberLastItem?.let { it + 1 } ?: 0
            val newCategoryItem = CategoryEntity("e$newId", name, iconID)
            categoryLocalDataSource.insert(newCategoryItem)
        }
    }

    override suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        withContext(defaultDispatcher) {
            val categoriesIncome: List<Category> =
                categoryLocalDataSource.getCategoriesIncome().map { it.toCategory() }
            val numberLastItem = categoriesIncome.lastOrNull()?.id?.substring(1)?.toInt()
            val newId = numberLastItem?.let { it + 1 } ?: 0
            val newCategoryItem = CategoryEntity("i$newId", name, iconID)
            categoryLocalDataSource.insert(newCategoryItem)
        }
    }

    override suspend fun initDefaultValue() {
        withContext(defaultDispatcher) {
            categoryLocalDataSource.initDefaultValue()
        }
    }
}