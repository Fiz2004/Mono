package com.fiz.mono.feature_category_edit.ui

import com.fiz.mono.core.domain.models.Category

data class CategoryEditUiState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
    var type: String = CategoryEditViewModel.TYPE_EXPENSE
)

val CategoryEditUiState.isRemoveButtonVisible: Boolean
    get() = allCategoryExpense.any { it.selected } ||
            allCategoryIncome.any { it.selected }
