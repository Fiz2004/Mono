package com.fiz.mono.category_edit.ui

sealed class CategoryEditUiEvent {
    data class ClickExpenseItem(val position: Int) : CategoryEditUiEvent()
    data class ClickIncomeItem(val position: Int) : CategoryEditUiEvent()
    object ClickBackButton : CategoryEditUiEvent()
    object ClickRemoveButton : CategoryEditUiEvent()
}