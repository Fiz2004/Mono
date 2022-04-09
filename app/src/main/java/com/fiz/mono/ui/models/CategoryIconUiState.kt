package com.fiz.mono.ui.models

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class CategoryIconUiState(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    val selected: Boolean = false
)

object CategoryIconItemDiff : DiffUtil.ItemCallback<CategoryIconUiState>() {
    override fun areItemsTheSame(
        oldItem: CategoryIconUiState,
        newItem: CategoryIconUiState
    ): Boolean {
        return oldItem.imgSrc == newItem.imgSrc
    }

    override fun areContentsTheSame(
        oldItem: CategoryIconUiState,
        newItem: CategoryIconUiState
    ): Boolean {
        return oldItem == newItem
    }
}