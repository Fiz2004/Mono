package com.fiz.mono.feature_category_add.ui

import com.fiz.mono.core.domain.models.CategoryIcon
import com.fiz.mono.core.ui.category_edit.CategoryEditViewModel

data class CategoryAddUiState(
    val allCategoryIcons: List<CategoryIcon> = listOf(),
    val nameCategory: String = "",
    val isReturn: Boolean = false,
    val type: String = CategoryEditViewModel.TYPE_EXPENSE
) {
    val isVisibilityAddButton: Boolean
        get() = allCategoryIcons.any { it.selected } && nameCategory.isNotBlank()
}