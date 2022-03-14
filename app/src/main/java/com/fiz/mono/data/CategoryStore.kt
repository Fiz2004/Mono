package com.fiz.mono.data

import com.fiz.mono.R

object CategoryStore {

    private val allCategoryExpense = mutableListOf(
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
        CategoryItem("Add more", null),
    )

    private val allCategoryIncome = mutableListOf(
        CategoryItem("Freelance", R.drawable.challenge),
        CategoryItem("Salary", R.drawable.money),
        CategoryItem("Bonus", R.drawable.coin),
        CategoryItem("Loan", R.drawable.user),
        CategoryItem("Add more", null),
    )

    fun getAllCategoryExpense() = allCategoryExpense
    fun getAllCategoryIncome() = allCategoryIncome

    fun insertNewCategoryExpense(newCategoryItem: CategoryItem) {
        allCategoryExpense.add(allCategoryExpense.size - 1, newCategoryItem)
    }

    fun insertNewCategoryIncome(newCategoryItem: CategoryItem) {
        allCategoryIncome.add(allCategoryIncome.size - 1, newCategoryItem)
    }

    fun removeCategoryExpense(position: Int) {
        allCategoryExpense.removeAt(position)
    }

    fun removeCategoryIncome(position: Int) {
        allCategoryIncome.removeAt(position)
    }

}