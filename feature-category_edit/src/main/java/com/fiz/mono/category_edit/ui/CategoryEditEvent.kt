package com.fiz.mono.category_edit.ui

sealed class CategoryEditEvent {
    data class ExpenseItemClicked(val position: Int) : CategoryEditEvent()
    data class IncomeItemClicked(val position: Int) : CategoryEditEvent()
    object BackButtonClicked : CategoryEditEvent()
    object RemoveButtonClicked : CategoryEditEvent()
}