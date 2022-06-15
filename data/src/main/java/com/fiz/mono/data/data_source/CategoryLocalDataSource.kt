package com.fiz.mono.data.data_source

import android.content.Context
import com.fiz.mono.data.AppDatabase
import com.fiz.mono.data.dao.CategoryDao
import com.fiz.mono.data.entity.CategoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryDao: CategoryDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : CategoryLocalDataSource {

    override val observeCategoriesExpense: Flow<List<CategoryEntity>> =
        categoryDao.getObserveAllExpense()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    override val observeCategoriesIncome: Flow<List<CategoryEntity>> =
        categoryDao.getObserveAllIncome()
            .distinctUntilChanged()
            .flowOn(defaultDispatcher)

    override suspend fun getCategoriesExpense(): List<CategoryEntity> {
        return withContext(defaultDispatcher) {
            categoryDao.getAllExpense()
        }
    }

    override suspend fun getCategoriesIncome(): List<CategoryEntity> {
        return withContext(defaultDispatcher) {
            categoryDao.getAllIncome()
        }
    }

    override suspend fun insert(newCategoryItem: CategoryEntity) {
        withContext(defaultDispatcher) {
            categoryDao.insert(newCategoryItem)
        }
    }

    override suspend fun delete(category: CategoryEntity) {
        withContext(defaultDispatcher) {
            categoryDao.delete(category)
        }
    }

    override suspend fun deleteAll() {
        withContext(defaultDispatcher) {
            categoryDao.deleteAll()

            addDefaultValues()
        }
    }

    private suspend fun addDefaultValues() {
        withContext(defaultDispatcher) {
            val allCategoryExpenseDefault = AppDatabase.getAllCategoryExpenseDefault(context)
            val allCategoryIncomeDefault = AppDatabase.getAllCategoryIncomeDefault(context)

            categoryDao.insertAll(allCategoryExpenseDefault)
            categoryDao.insertAll(allCategoryIncomeDefault)
        }
    }

}

interface CategoryLocalDataSource {

    val observeCategoriesExpense: Flow<List<CategoryEntity>>

    val observeCategoriesIncome: Flow<List<CategoryEntity>>

    suspend fun getCategoriesExpense(): List<CategoryEntity>

    suspend fun getCategoriesIncome(): List<CategoryEntity>

    suspend fun insert(newCategoryItem: CategoryEntity)

    suspend fun delete(category: CategoryEntity)

    suspend fun deleteAll()

}