package com.fiz.mono.database.repositories

import com.fiz.mono.database.data_source.CategoryLocalDataSource
import com.fiz.mono.database.entity.CategoryEntity
import com.fiz.mono.database.mapper.toCategory
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val editString: String,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : CategoryRepository {
    override var allCategoryExpense: Flow<List<Category>> =
        categoryLocalDataSource.allCategoryExpense
            .map {
                it.map { it.toCategory() }
            }.flowOn(defaultDispatcher)

    override var allCategoryIncome: Flow<List<Category>> =
        categoryLocalDataSource.allCategoryIncome
            .map {
                it.map { it.toCategory() }
            }.flowOn(defaultDispatcher)

    override fun getAllCategoryExpenseForInput(): Flow<Resource<List<Category>>> {
        return flow {
            emit(Resource.Loading(true))
            allCategoryExpense.map {
                val editCategory = Category("e", editString, 0)
                it + editCategory
            }.collect {
                emit(Resource.Success(it))
            }
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }.flowOn(defaultDispatcher)
    }

    override fun getAllCategoryIncomeForInput(): Flow<List<Category>> {
        return allCategoryIncome
            .map { it + Category("i", editString, 0) }
            .flowOn(defaultDispatcher)
    }

    override suspend fun removeCategory(category: Category) =
        withContext(defaultDispatcher) {
            categoryLocalDataSource.delete(category)
        }

    override suspend fun insertNewCategoryExpense(name: String, iconID: String) =
        withContext(defaultDispatcher) {
            val result: List<Category> = allCategoryExpense.first()
            val numberLastItem = result.lastOrNull()?.id?.substring(1)?.toInt()
            val newId = numberLastItem?.let { it + 1 } ?: 0
            val newCategoryItem = CategoryEntity("e$newId", name, iconID)
            categoryLocalDataSource.insert(newCategoryItem)
        }

    override suspend fun insertNewCategoryIncome(name: String, iconID: String) =
        withContext(defaultDispatcher) {
            val result: List<Category> = allCategoryIncome.first()
            val numberLastItem = result.lastOrNull()?.id?.substring(1)?.toInt()
            val newId = numberLastItem?.let { it + 1 } ?: 0
            val newCategoryItem = CategoryEntity("i$newId", name, iconID)
            categoryLocalDataSource.insert(newCategoryItem)
        }
}