package com.fiz.mono.category_edit.ui

import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.TypeTransaction

data class CategoryEditViewState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
    var type: TypeTransaction = TypeTransaction.Expense
)

val CategoryEditViewState.isRemoveButtonVisible: Boolean
    get() = allCategoryExpense.any { it.selected } ||
            allCategoryIncome.any { it.selected }
