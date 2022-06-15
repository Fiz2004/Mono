package com.fiz.mono.report.ui.select

import com.fiz.mono.domain.models.Category

data class SelectViewState(
    val allCategoryExpense: List<Category> = listOf(),
    val allCategoryIncome: List<Category> = listOf(),
)