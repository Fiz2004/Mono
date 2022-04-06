package com.fiz.mono.data.entity

import androidx.annotation.DrawableRes
import com.fiz.mono.ui.models.CategoryIconUiState

data class CategoryIcon(
    val id: String,
    @DrawableRes
    val imgSrc: Int
) {
    fun toCategoryIconUiState(): CategoryIconUiState {
        return CategoryIconUiState(
            id = this.id,
            imgSrc = this.imgSrc,
            selected = false
        )
    }
}