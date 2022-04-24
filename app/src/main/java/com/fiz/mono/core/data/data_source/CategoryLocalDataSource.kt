package com.fiz.mono.core.data.data_source

import android.content.Context
import com.fiz.mono.core.data.mapper.toCategoryEntity
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.database.AppDatabase
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.entity.CategoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryLocalDataSource @Inject constructor(
    private val categoryDao: CategoryDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    var allCategoryExpense: Flow<List<CategoryEntity>> =
        categoryDao.getAllExpense()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    var allCategoryIncome: Flow<List<CategoryEntity>> =
        categoryDao.getAllIncome()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    suspend fun insert(newCategoryItem: CategoryEntity) = withContext(defaultDispatcher) {
        categoryDao.insert(newCategoryItem)
    }

    suspend fun delete(category: Category) = withContext(defaultDispatcher) {
        categoryDao.delete(category.toCategoryEntity())
    }

    suspend fun deleteAll(context: Context) = withContext(defaultDispatcher) {
        categoryDao.deleteAll()

        addDefaultValues(context)
    }

    private suspend fun addDefaultValues(context: Context) = withContext(defaultDispatcher) {
        val allCategoryExpenseDefault = AppDatabase.getAllCategoryExpenseDefault(context)
        val allCategoryIncomeDefault = AppDatabase.getAllCategoryIncomeDefault(context)

        categoryDao.insertAll(allCategoryExpenseDefault)
        categoryDao.insertAll(allCategoryIncomeDefault)
    }

}