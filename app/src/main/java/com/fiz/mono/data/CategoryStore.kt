package com.fiz.mono.data

import com.fiz.mono.R
import com.fiz.mono.data.database.CategoryItemDAO
import com.fiz.mono.data.database.CategoryItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

object CategoryStore {
    private lateinit var allCategoryExpense: MutableList<CategoryItem>
    private lateinit var allCategoryIncome: MutableList<CategoryItem>

    private val categoryItemDao: CategoryItemDAO? =
        CategoryItemDatabase.getDatabase()?.expenseCategoryItemDao()

    suspend fun init(callback: () -> Unit) {
        var loadItemDao: List<CategoryItem>? = null

        try {
            loadItemDao = categoryItemDao?.getAll()
            if (loadItemDao?.size == 0 || loadItemDao == null) {
                allCategoryExpense = mutableListOf(
                    CategoryItem("e0", "Bank", R.drawable.bank),
                    CategoryItem("e1", "Food", R.drawable.food),
                    CategoryItem("e2", "Medican", R.drawable.medican),
                    CategoryItem("e3", "Gym", R.drawable.gym),
                    CategoryItem("e4", "Coffee", R.drawable.coffee),
                    CategoryItem("e5", "Shopping", R.drawable.market),
                    CategoryItem("e6", "Cats", R.drawable.cat),
                    CategoryItem("e7", "Party", R.drawable.party),
                    CategoryItem("e8", "Gift", R.drawable.gift),
                    CategoryItem("e9", "Gas", R.drawable.gas),
                )
                withContext(Dispatchers.Default) {
                    allCategoryExpense.forEach {
                        categoryItemDao?.insert(it)
                    }
                }
            } else {
                val loadItem = loadItemDao
                allCategoryExpense =
                    loadItem.filter { it.id[0] == 'e' } as MutableList<CategoryItem>
            }

            if (loadItemDao?.size == 0 || loadItemDao == null) {
                allCategoryIncome = mutableListOf(
                    CategoryItem("i0", "Freelance", R.drawable.challenge),
                    CategoryItem("i1", "Salary", R.drawable.money),
                    CategoryItem("i2", "Bonus", R.drawable.coin),
                    CategoryItem("i3", "Loan", R.drawable.user),
                )
                withContext(Dispatchers.Default) {
                    allCategoryIncome.forEach {
                        categoryItemDao?.insert(it)
                    }
                }
            } else {
                val loadItem = loadItemDao
                allCategoryIncome = loadItem.filter { it.id[0] == 'i' } as MutableList<CategoryItem>
            }
            callback()

        } catch (e: Exception) {
            allCategoryExpense = mutableListOf(
                CategoryItem("e0", "Bank", R.drawable.bank),
                CategoryItem("e1", "Food", R.drawable.food),
                CategoryItem("e2", "Medican", R.drawable.medican),
                CategoryItem("e3", "Gym", R.drawable.gym),
                CategoryItem("e4", "Coffee", R.drawable.coffee),
                CategoryItem("e5", "Shopping", R.drawable.market),
                CategoryItem("e6", "Cats", R.drawable.cat),
                CategoryItem("e7", "Party", R.drawable.party),
                CategoryItem("e8", "Gift", R.drawable.gift),
                CategoryItem("e9", "Gas", R.drawable.gas),
            )
            withContext(Dispatchers.Default) {
                allCategoryExpense.forEach {
                    categoryItemDao?.insert(it)
                }
            }

            allCategoryIncome = mutableListOf(
                CategoryItem("i0", "Freelance", R.drawable.challenge),
                CategoryItem("i1", "Salary", R.drawable.money),
                CategoryItem("i2", "Bonus", R.drawable.coin),
                CategoryItem("i3", "Loan", R.drawable.user),
            )
            withContext(Dispatchers.Default) {
                allCategoryExpense.forEach {
                    categoryItemDao?.insert(it)
                }
            }
            callback()
        }
    }

    fun getAllCategoryExpenseForEdit(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryExpense)
        result.add(CategoryItem("e", "Add more", null))
        return result
    }

    fun getAllCategoryIncomeForEdit(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryIncome)
        result.add(CategoryItem("i", "Add more", null))
        return result
    }

    fun getAllCategoryExpenseForInput(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryExpense)
        result.add(CategoryItem("e", "Edit", null))
        return result
    }

    fun getAllCategoryIncomeForInput(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryIncome)
        result.add(CategoryItem("i", "Edit", null))
        return result
    }

    fun insertNewCategoryExpense(name: String, icon: Int) {
        val numberLastItem = allCategoryExpense.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("e$newId", name, icon)

        allCategoryExpense.add(newCategoryItem)
        Executors.newSingleThreadExecutor().execute {
            categoryItemDao?.insert(newCategoryItem)
        }
    }

    fun insertNewCategoryIncome(name: String, icon: Int) {
        val numberLastItem = allCategoryIncome.lastOrNull()?.id?.substring(1)?.toInt()
        val newId = numberLastItem?.let { it + 1 } ?: 0

        val newCategoryItem = CategoryItem("i$newId", name, icon)

        allCategoryIncome.add(newCategoryItem)
        Executors.newSingleThreadExecutor().execute {
            categoryItemDao?.insert(newCategoryItem)
        }
    }

    fun removeCategoryExpense(position: Int) {
        Executors.newSingleThreadExecutor().execute {
            categoryItemDao?.delete(allCategoryExpense[position])
        }
        allCategoryExpense.removeAt(position)
    }

    fun removeCategoryIncome(position: Int) {
        Executors.newSingleThreadExecutor().execute {
            categoryItemDao?.delete(allCategoryExpense[position])
        }
        allCategoryIncome.removeAt(position)
    }

}