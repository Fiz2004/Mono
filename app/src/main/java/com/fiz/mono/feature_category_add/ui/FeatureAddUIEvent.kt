package com.fiz.mono.feature_category_add.ui

sealed class FeatureAddUIEvent {
    data class Loading(val type: String) : FeatureAddUIEvent()
    object ClickBackButton : FeatureAddUIEvent()
    object OnClickBackButton : FeatureAddUIEvent()
    object ClickAddButton : FeatureAddUIEvent()
    data class ChangeCategoryName(val newCategoryName: String) : FeatureAddUIEvent()
    data class ClickCategory(val position: Int) : FeatureAddUIEvent()
}