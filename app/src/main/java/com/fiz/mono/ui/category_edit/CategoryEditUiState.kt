package com.fiz.mono.ui.category_edit

import com.fiz.mono.ui.models.CategoryUiState

data class CategoryEditUiState(
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val isReturn: Boolean = false,
    val isMoveAdd: Boolean = false,
    var type: String = CategoryEditViewModel.TYPE_EXPENSE
) {

    val isSelected: Boolean
        get() = allCategoryExpense.any { it.selected } ||
                allCategoryIncome.any { it.selected }

    fun clickExpenseRecyclerView(position: Int): CategoryEditUiState {
        if (position == allCategoryExpense.size - 1) {
            val allCategoryExpense = allCategoryExpense.map { it.copy(selected = false) }
            val allCategoryIncome = allCategoryIncome.map { it.copy(selected = false) }
            val type = CategoryEditViewModel.TYPE_EXPENSE
            val isMoveAdd = true

            return copy(
                allCategoryExpense = allCategoryExpense,
                allCategoryIncome = allCategoryIncome,
                type = type,
                isMoveAdd = isMoveAdd
            )
        }

        val allCategoryExpense =
            allCategoryExpense.mapIndexed { index, categoryUiState ->
                var selected = categoryUiState.selected
                if (index != position && categoryUiState.selected)
                    selected = false
                if (index == position)
                    selected = !selected
                categoryUiState.copy(
                    selected =
                    selected
                )
            }
        val allCategoryIncome = allCategoryIncome.map {
            var selected = it.selected
            if (it.selected)
                selected = false
            it.copy(selected = selected)
        }

        return copy(
            allCategoryExpense = allCategoryExpense,
            allCategoryIncome = allCategoryIncome,
        )
    }

    fun clickIncomeRecyclerView(position: Int): CategoryEditUiState {
        if (position == allCategoryIncome.size - 1) {
            val allCategoryExpense = allCategoryExpense.map { it.copy(selected = false) }
            val allCategoryIncome = allCategoryIncome.map { it.copy(selected = false) }
            val type = CategoryEditViewModel.TYPE_INCOME
            val isMoveAdd = true

            return copy(
                allCategoryExpense = allCategoryExpense,
                allCategoryIncome = allCategoryIncome,
                type = type,
                isMoveAdd = isMoveAdd
            )
        }

        val allCategoryIncome =
            allCategoryIncome.mapIndexed { index, categoryUiState ->
                var selected = categoryUiState.selected
                if (index != position && categoryUiState.selected)
                    selected = false
                if (index == position)
                    selected = !selected
                categoryUiState.copy(
                    selected =
                    selected
                )
            }
        val allCategoryExpense = allCategoryExpense.map {
            var selected = it.selected
            if (it.selected)
                selected = false
            it.copy(selected = selected)
        }

        return copy(
            allCategoryExpense = allCategoryExpense,
            allCategoryIncome = allCategoryIncome,
        )
    }

}