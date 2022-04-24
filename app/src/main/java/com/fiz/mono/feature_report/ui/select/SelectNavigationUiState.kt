package com.fiz.mono.feature_report.ui.select

import com.fiz.mono.feature_category_edit.ui.CategoryEditViewModel.Companion.TYPE_EXPENSE

data class SelectNavigationUiState(
    val isMove: Boolean = false,
    val id: String = "",
    val type: String = TYPE_EXPENSE
)