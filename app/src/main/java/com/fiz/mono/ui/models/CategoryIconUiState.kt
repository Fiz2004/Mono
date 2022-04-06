package com.fiz.mono.ui.models

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.data.entity.CategoryIcon

data class CategoryIconUiState(
    val id: String,
    @DrawableRes
    val imgSrc: Int,
    val selected: Boolean = false
) {
    fun toCategoryIcon(): CategoryIcon {
        return CategoryIcon(
            id = this.id,
            imgSrc = this.imgSrc
        )
    }

    fun selectedFalse(): CategoryIconUiState {
        return copy(selected = false)
    }

    fun invertSelected(): CategoryIconUiState {
        return copy(selected = !this.selected)
    }
}

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