package com.fiz.mono.category_add.ui

import com.fiz.mono.domain.models.TypeTransaction

sealed class CategoryAddEvent {
    data class ActivityLoaded(val type: TypeTransaction) : CategoryAddEvent()
    object BackButtonClicked : CategoryAddEvent()
    object AddButtonClicked : CategoryAddEvent()
    data class CategoryNameChanged(val newCategoryName: String) : CategoryAddEvent()
    data class CategoryClicked(val position: Int) : CategoryAddEvent()
}