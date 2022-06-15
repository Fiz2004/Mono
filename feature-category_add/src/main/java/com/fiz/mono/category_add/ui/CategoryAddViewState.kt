package com.fiz.mono.category_add.ui

import com.fiz.mono.domain.models.CategoryIcon
import com.fiz.mono.domain.models.TypeTransaction

data class CategoryAddViewState(
    val allCategoryIcons: List<CategoryIcon> = listOf(),
    val nameCategory: String = "",
    val type: TypeTransaction = TypeTransaction.Expense
) {
    val isVisibilityAddButton: Boolean
        get() = allCategoryIcons.any { it.selected } && nameCategory.isNotBlank()
}

