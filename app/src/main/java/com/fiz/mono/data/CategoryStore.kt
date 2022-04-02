package com.fiz.mono.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.fiz.mono.R
import com.fiz.mono.data.database.dao.CategoryDao

class CategoryStore(
    private val categoryDao: CategoryDao,
    private val editString: String,
    private val addMoreString: String
) {
    var allCategoryExpense: LiveData<List<CategoryItem>> =
        categoryDao.getAllExpense().asLiveData()
    var allCategoryIncome: LiveData<List<CategoryItem>> =
        categoryDao.getAllIncome().asLiveData()

    fun getAllCategoryExpenseForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", addMoreString, ""))
            result
        }
    }

    fun getAllCategoryIncomeForEdit(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", addMoreString, ""))
            result
        }
    }

    fun getAllCategoryExpenseForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryExpense) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("e", editString, ""))
            result
        }
    }

    fun getAllCategoryIncomeForInput(): LiveData<List<CategoryItem>> {
        return Transformations.map(allCategoryIncome) {
            val result = emptyList<CategoryItem>().toMutableList()
            result.addAll(it)
            result.add(CategoryItem("i", editString, ""))
            result
        }
    }

    suspend fun insertNewCategoryExpense(name: String, iconID: String) {
        val numberLastItem = allCategoryExpense.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("e$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    suspend fun insertNewCategoryIncome(name: String, iconID: String) {
        val numberLastItem = allCategoryIncome.value?.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("i$newId", name, iconID)

        categoryDao.insert(newCategoryItem)
    }

    suspend fun removeCategoryExpense(position: Int) {
        allCategoryExpense.value?.get(position)?.let {
            categoryDao.delete(it)
        }
    }

    suspend fun removeCategoryIncome(position: Int) {
        allCategoryIncome.value?.get(position)?.let {
            categoryDao.delete(it)
        }
    }

    suspend fun deleteAll(context: Context) {
        allCategoryExpense.value?.map {
            categoryDao.delete(it)
        }
        allCategoryIncome.value?.map {
            categoryDao.delete(it)
        }

        val allCategoryExpenseDefault = mutableListOf(
            CategoryItem("e0", context.getString(R.string.bank), "bank"),
            CategoryItem("e1", context.getString(R.string.food), "food"),
            CategoryItem(
                "e2",
                context.getString(R.string.medican),
                "medican"
            ),
            CategoryItem("e3", context.getString(R.string.gym), "gym"),
            CategoryItem(
                "e4",
                context.getString(R.string.coffee),
                "coffee"
            ),
            CategoryItem(
                "e5",
                context.getString(R.string.shopping),
                "market"
            ),
            CategoryItem("e6", context.getString(R.string.cats), "cat"),
            CategoryItem("e7", context.getString(R.string.party), "party"),
            CategoryItem("e8", context.getString(R.string.gift), "gift"),
            CategoryItem("e9", context.getString(R.string.gas), "gas"),
        )
        val allCategoryIncomeDefault = mutableListOf(
            CategoryItem(
                "i0",
                context.getString(R.string.freelance),
                "challenge"
            ),
            CategoryItem("i1", context.getString(R.string.salary), "money"),
            CategoryItem("i2", context.getString(R.string.bonus), "coin"),
            CategoryItem("i3", context.getString(R.string.loan), "user"),
        )
        allCategoryExpenseDefault.forEach {
            categoryDao.insert(it)
        }
        allCategoryIncomeDefault.forEach {
            categoryDao.insert(it)
        }
    }

}