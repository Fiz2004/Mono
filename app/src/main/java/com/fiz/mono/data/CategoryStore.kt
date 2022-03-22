package com.fiz.mono.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fiz.mono.data.database.CategoryItemDAO

class CategoryStore(private val categoryItemDao: CategoryItemDAO) {
    var allCategoryExpense: LiveData<List<CategoryItem>> = categoryItemDao.getAllExpense()
    var allCategoryIncome: LiveData<List<CategoryItem>> = categoryItemDao.getAllIncome()

    fun getAllCategoryExpenseForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", "Add more", ""))
            result
        }
    }

    fun getAllCategoryIncomeForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", "Add more", ""))
            result
        }
    }

    fun getAllCategoryExpenseForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", "Edit", ""))
            result
        }
    }

    fun getAllCategoryIncomeForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", "Edit", ""))
            result
        }
    }

    suspend fun insertNewCategoryExpense(name: String, iconID: String) {
        val numberLastItem = allCategoryExpense.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("e$newId", name, iconID)

        categoryItemDao.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        val numberLastItem = allCategoryIncome.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("i$newId", name, iconID)

        categoryItemDao.insert(newCategoryItem)
    }

    suspend fun removeCategoryExpense(position: Int) {
        allCategoryExpense.value?.get(position)?.let {
            categoryItemDao.delete(it)
        }
    }

    suspend fun removeCategoryIncome(position: Int) {
        allCategoryIncome.value?.get(position)?.let {
            categoryItemDao.delete(it)
        }
    }

}