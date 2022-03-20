package com.fiz.mono.data

import com.fiz.mono.R
import com.fiz.mono.data.database.CategoryItemDatabase
import com.fiz.mono.data.database.ExpenseCategoryItemDAO
import com.fiz.mono.data.database.IncomeCategoryItemDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object CategoryStore {
    private lateinit var allCategoryExpense: MutableList<CategoryItem>
    private lateinit var allCategoryIncome: MutableList<CategoryItem>

    private val expenseCategoryItemDao: ExpenseCategoryItemDAO? =
        CategoryItemDatabase.getDatabase()?.expenseCategoryItemDao()
    private val incomeCategoryItemDao: IncomeCategoryItemDAO? =
        CategoryItemDatabase.getDatabase()?.incomeCategoryItemDao()

    fun init() {
        var loadExpense: List<CategoryItem>? = null
        try {
            CoroutineScope(Dispatchers.IO).launch {
                loadExpense = expenseCategoryItemDao?.getAll()
                if (loadExpense == null) {
                    allCategoryExpense = mutableListOf(
                        CategoryItem("Bank", R.drawable.bank),
                        CategoryItem("Food", R.drawable.food),
                        CategoryItem("Medican", R.drawable.medican),
                        CategoryItem("Gym", R.drawable.gym),
                        CategoryItem("Coffee", R.drawable.coffee),
                        CategoryItem("Shopping", R.drawable.market),
                        CategoryItem("Cats", R.drawable.cat),
                        CategoryItem("Party", R.drawable.party),
                        CategoryItem("Gift", R.drawable.gift),
                        CategoryItem("Gas", R.drawable.gas),
                    )
                    Executors.newSingleThreadExecutor().execute {
                        allCategoryExpense.forEach {
                            expenseCategoryItemDao?.insert(it)
                        }
                    }
                } else {
                    allCategoryExpense = loadExpense as MutableList<CategoryItem>
                }
            }
        } catch (e: Exception) {
            allCategoryExpense = mutableListOf(
                CategoryItem("Bank", R.drawable.bank),
                CategoryItem("Food", R.drawable.food),
                CategoryItem("Medican", R.drawable.medican),
                CategoryItem("Gym", R.drawable.gym),
                CategoryItem("Coffee", R.drawable.coffee),
                CategoryItem("Shopping", R.drawable.market),
                CategoryItem("Cats", R.drawable.cat),
                CategoryItem("Party", R.drawable.party),
                CategoryItem("Gift", R.drawable.gift),
                CategoryItem("Gas", R.drawable.gas),
            )
        }

        var loadIncome: List<CategoryItem>? = null
        try {
            CoroutineScope(Dispatchers.IO).launch {
                loadIncome = incomeCategoryItemDao?.getAll()
                if (loadIncome == null) {
                    allCategoryIncome = mutableListOf(
                        CategoryItem("Freelance", R.drawable.challenge),
                        CategoryItem("Salary", R.drawable.money),
                        CategoryItem("Bonus", R.drawable.coin),
                        CategoryItem("Loan", R.drawable.user),
                    )
                    Executors.newSingleThreadExecutor().execute {
                        allCategoryIncome.forEach {
                            expenseCategoryItemDao?.insert(it)
                        }
                    }
                } else {
                    allCategoryIncome = loadIncome as MutableList<CategoryItem>
                }
            }
        } catch (e: Exception) {
            allCategoryIncome = mutableListOf(
                CategoryItem("Freelance", R.drawable.challenge),
                CategoryItem("Salary", R.drawable.money),
                CategoryItem("Bonus", R.drawable.coin),
                CategoryItem("Loan", R.drawable.user),
            )
        }

    }

    fun getAllCategoryExpenseForEdit(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryExpense)
        result.add(CategoryItem("Add more", null))
        return result
    }

    fun getAllCategoryIncomeForEdit(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryIncome)
        result.add(CategoryItem("Add more", null))
        return result
    }

    fun getAllCategoryExpenseForInput(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryExpense)
        result.add(CategoryItem("Edit", null))
        return result
    }

    fun getAllCategoryIncomeForInput(): MutableList<CategoryItem> {
        val result = emptyList<CategoryItem>().toMutableList()
        result.addAll(allCategoryIncome)
        result.add(CategoryItem("Edit", null))
        return result
    }

    fun insertNewCategoryExpense(newCategoryItem: CategoryItem) {
        allCategoryExpense.add(newCategoryItem)
        Executors.newSingleThreadExecutor().execute {
            expenseCategoryItemDao?.insert(newCategoryItem)
        }
    }

    fun insertNewCategoryIncome(newCategoryItem: CategoryItem) {
        allCategoryIncome.add(newCategoryItem)
        Executors.newSingleThreadExecutor().execute {
            incomeCategoryItemDao?.insert(newCategoryItem)
        }
    }

    fun removeCategoryExpense(position: Int) {
        Executors.newSingleThreadExecutor().execute {
            expenseCategoryItemDao?.delete(allCategoryExpense[position])
        }
        allCategoryExpense.removeAt(position)

    }

    fun removeCategoryIncome(position: Int) {
        Executors.newSingleThreadExecutor().execute {
            incomeCategoryItemDao?.delete(allCategoryExpense[position])
        }
        allCategoryIncome.removeAt(position)
    }

}