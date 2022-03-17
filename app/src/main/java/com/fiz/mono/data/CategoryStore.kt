package com.fiz.mono.data

import com.fiz.mono.R

object CategoryStore {

    private val allCategoryIcon = mutableListOf(
        CategoryIconItem(R.drawable.user),
        CategoryIconItem(R.drawable.plane),
        CategoryIconItem(R.drawable.chair),
        CategoryIconItem(R.drawable.baby),
        CategoryIconItem(R.drawable.bank),
        CategoryIconItem(R.drawable.gym),
        CategoryIconItem(R.drawable.cycles),
        CategoryIconItem(R.drawable.bird),
        CategoryIconItem(R.drawable.boat),
        CategoryIconItem(R.drawable.books),
        CategoryIconItem(R.drawable.brain),
        CategoryIconItem(R.drawable.building),
        CategoryIconItem(R.drawable.birthday),
        CategoryIconItem(R.drawable.camera),
        CategoryIconItem(R.drawable.car),
        CategoryIconItem(R.drawable.cat),
        CategoryIconItem(R.drawable.study),
        CategoryIconItem(R.drawable.coffee),
        CategoryIconItem(R.drawable.coin),
        CategoryIconItem(R.drawable.pie),
        CategoryIconItem(R.drawable.cook),
        CategoryIconItem(R.drawable.coin),
        CategoryIconItem(R.drawable.dog),
        CategoryIconItem(R.drawable.facemask),
        CategoryIconItem(R.drawable.medican),
        CategoryIconItem(R.drawable.flower),
        CategoryIconItem(R.drawable.dinner),
        CategoryIconItem(R.drawable.gas),
        CategoryIconItem(R.drawable.gift),
        CategoryIconItem(R.drawable.bag),
        CategoryIconItem(R.drawable.challenge),
        CategoryIconItem(R.drawable.music),
        CategoryIconItem(R.drawable.house),
        CategoryIconItem(R.drawable.map),
        CategoryIconItem(R.drawable.glass),
        CategoryIconItem(R.drawable.money),
        CategoryIconItem(R.drawable.package1),
        CategoryIconItem(R.drawable.run),
        CategoryIconItem(R.drawable.pill),
        CategoryIconItem(R.drawable.food),
        CategoryIconItem(R.drawable.`fun`),
        CategoryIconItem(R.drawable.receipt),
        CategoryIconItem(R.drawable.lawer),
        CategoryIconItem(R.drawable.market),
        CategoryIconItem(R.drawable.shower),
        CategoryIconItem(R.drawable.football),
        CategoryIconItem(R.drawable.store),
        CategoryIconItem(R.drawable.study),
        CategoryIconItem(R.drawable.tennis_ball),
        CategoryIconItem(R.drawable.wc),
        CategoryIconItem(R.drawable.train),
        CategoryIconItem(R.drawable.cup),
        CategoryIconItem(R.drawable.clothes),
        CategoryIconItem(R.drawable.wallet),
        CategoryIconItem(R.drawable.sea),
        CategoryIconItem(R.drawable.party),
        CategoryIconItem(R.drawable.fix)
    )

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
    )

    private val allCategoryIncome = mutableListOf(
        CategoryItem("Freelance", R.drawable.challenge),
        CategoryItem("Salary", R.drawable.money),
        CategoryItem("Bonus", R.drawable.coin),
        CategoryItem("Loan", R.drawable.user),
    )

    fun getAllCategoryExpense() = allCategoryExpense
    fun getAllCategoryIncome() = allCategoryIncome
    fun getAllCategoryIcon() = allCategoryIcon

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
    }

    fun insertNewCategoryIncome(newCategoryItem: CategoryItem) {
        allCategoryIncome.add(newCategoryItem)
    }

    fun removeCategoryExpense(position: Int) {
        allCategoryExpense.removeAt(position)
    }

    fun removeCategoryIncome(position: Int) {
        allCategoryIncome.removeAt(position)
    }

}