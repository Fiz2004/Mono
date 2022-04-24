package com.fiz.mono.feature_report.ui.select

import com.fiz.mono.core.domain.models.Category

data class SelectUiState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
)