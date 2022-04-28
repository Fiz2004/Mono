package com.fiz.mono.category_add.ui

import com.fiz.mono.domain.models.CategoryIcon

data class CategoryAddUiState(
    val allCategoryIcons: List<CategoryIcon> = listOf(),
    val nameCategory: String = "",
    val isReturn: Boolean = false,
    val type: String = "expense"
) {
    val isVisibilityAddButton: Boolean
        get() = allCategoryIcons.any { it.selected } && nameCategory.isNotBlank()
}