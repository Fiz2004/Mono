package com.fiz.mono.core.data.repositories

import com.fiz.mono.core.data.data_source.CategoryDataSource
import com.fiz.mono.core.data.mapper.toCategory
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.core.util.Resource
import com.fiz.mono.database.entity.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class CategoryRepositoryImpl(
    private val categoryDataSource: CategoryDataSource,
    private val editString: String,
    private val addMoreString: String
) : CategoryRepository {
    val scope = CoroutineScope(Dispatchers.Default)

    override var allCategoryExpense: Flow<List<Category>> =
        categoryDataSource.allCategoryExpense.map { it.map { it.toCategory() } }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    override var allCategoryIncome: Flow<List<Category>> =
        categoryDataSource.allCategoryIncome.map { it.map { it.toCategory() } }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    override fun getAllCategoryExpenseForEdit(): Flow<List<Category>> {
        return allCategoryExpense.map {
            it + Category(
                "e",
                addMoreString,
                0
            )
        }
    }

    override fun getAllCategoryIncomeForEdit(): Flow<List<Category>> {
        return allCategoryIncome.map {
            it + Category(
                "i",
                addMoreString,
                0
            )
        }
    }

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
        }.flowOn(Dispatchers.Default)
    }

    override fun getAllCategoryIncomeForInput(): Flow<List<Category>> {
        return allCategoryIncome.map { it + Category("i", editString, 0) }
    }

    override suspend fun removeCategoryExpense(category: Category) {
        categoryDataSource.delete(category)
    }

    override suspend fun removeCategoryIncome(category: Category) {
        categoryDataSource.delete(category)
    }

    override suspend fun insertNewCategoryExpense(name: String, iconID: String) {
        val result: List<Category> = allCategoryExpense.first()
        val numberLastItem = result.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0
        val newCategoryItem = CategoryEntity("e$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }

    override suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        val result: List<Category> = allCategoryIncome.first()
        val numberLastItem = result.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0
        val newCategoryItem = CategoryEntity("i$newId", name, iconID)
        categoryDataSource.insert(newCategoryItem)
    }
}