package com.fiz.mono.ui.category_add

import com.fiz.mono.ui.category_edit.CategoryEditViewModel
import com.fiz.mono.ui.models.CategoryIconUiState

data class CategoryAddUiState(
    val allCategoryIcons: List<CategoryIconUiState> = listOf(),
    val nameCategory: String = "",
    val isReturn: Boolean = false,
    val type: String = CategoryEditViewModel.TYPE_EXPENSE
) {
    val isVisibilityAddButton: Boolean
        get() = allCategoryIcons.any { it.selected } && nameCategory.isNotBlank()

    fun init(type: String): CategoryAddUiState {
        return copy(
            type = type,
            allCategoryIcons = allCategoryIcons
        )
    }

    fun clickRecyclerView(position: Int): CategoryAddUiState {
        val allCategoryIcons = allCategoryIcons.mapIndexed { index, categoryIconUiState ->
            var selected = categoryIconUiState.selected
            if (index != position && categoryIconUiState.selected)
                selected = false
            if (index == position)
                selected = !selected
            categoryIconUiState.copy(
                selected =
                selected
            )
        }
        return copy(
            allCategoryIcons = allCategoryIcons
        )
    }


    fun setCategoryName(text: CharSequence?): CategoryAddUiState {
        return copy(nameCategory = text.toString())
    }
}