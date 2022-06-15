package com.fiz.mono.category_add.ui

import com.fiz.mono.domain.models.TypeTransaction

sealed class FeatureAddEvent {
    data class ActivityLoaded(val type: TypeTransaction) : FeatureAddEvent()
    object BackButtonClicked : FeatureAddEvent()
    object AddButtonClicked : FeatureAddEvent()
    data class CategoryNameChanged(val newCategoryName: String) : FeatureAddEvent()
    data class CategoryClicked(val position: Int) : FeatureAddEvent()
}