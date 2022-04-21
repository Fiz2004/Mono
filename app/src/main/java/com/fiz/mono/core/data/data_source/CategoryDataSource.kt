package com.fiz.mono.core.data.data_source

import android.content.Context
import com.fiz.mono.R
import com.fiz.mono.core.data.mapper.toCategoryEntity
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.database.dao.CategoryDao
import com.fiz.mono.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryDataSource @Inject constructor(private val categoryDao: CategoryDao) {

    var allCategoryExpense: Flow<List<CategoryEntity>> =
        categoryDao.getAllExpense().distinctUntilChanged()

    var allCategoryIncome: Flow<List<CategoryEntity>> =
        categoryDao.getAllIncome().distinctUntilChanged()

    suspend fun insert(newCategoryItem: CategoryEntity) {
        categoryDao.insert(newCategoryItem)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category.toCategoryEntity())
    }

    suspend fun deleteAll(context: Context) {
        categoryDao.deleteAll()

        addDefaultValues(context)
    }

    private suspend fun addDefaultValues(context: Context) {
        val allCategoryExpenseDefault = getAllCategoryExpenseDefault(context)
        val allCategoryIncomeDefault = getAllCategoryIncomeDefault(context)

        categoryDao.insertAll(allCategoryExpenseDefault)
        categoryDao.insertAll(allCategoryIncomeDefault)
    }

}

fun getAllCategoryExpenseDefault(context: Context) = mutableListOf(
    CategoryEntity("e0", context.getString(R.string.bank), "bank"),
    CategoryEntity("e1", context.getString(R.string.food), "food"),
    CategoryEntity("e2", context.getString(R.string.medican), "medican"),
    CategoryEntity("e3", context.getString(R.string.gym), "gym"),
    CategoryEntity("e4", context.getString(R.string.coffee), "coffee"),
    CategoryEntity("e5", context.getString(R.string.shopping), "market"),
    CategoryEntity("e6", context.getString(R.string.cats), "cat"),
    CategoryEntity("e7", context.getString(R.string.party), "party"),
    CategoryEntity("e8", context.getString(R.string.gift), "gift"),
    CategoryEntity("e9", context.getString(R.string.gas), "gas"),
)

fun getAllCategoryIncomeDefault(context: Context) = mutableListOf(
    CategoryEntity("i0", context.getString(R.string.freelance), "challenge"),
    CategoryEntity("i1", context.getString(R.string.salary), "money"),
    CategoryEntity("i2", context.getString(R.string.bonus), "coin"),
    CategoryEntity("i3", context.getString(R.string.loan), "user"),
)