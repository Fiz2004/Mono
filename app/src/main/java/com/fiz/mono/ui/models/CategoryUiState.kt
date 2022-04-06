package com.fiz.mono.ui.models

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.data.data_source.CategoryIconDataSource
import com.fiz.mono.data.entity.Category

data class CategoryUiState(
    val id: String,
    val name: String,
    val imgSrc: Int,
    val selected: Boolean = false
) {
    fun toCategory(): Category {
        return Category(
            id = this.id,
            name = this.name,
            mapImgSrc = CategoryIconDataSource().getIDCategoryIcon(this.imgSrc),
        )
    }

    fun selectedFalse(): CategoryUiState {
        return copy(selected = false)
    }

    fun invertSelected(): CategoryUiState {
        return copy(selected = !this.selected)
    }
}

object CategoryItemDiff : DiffUtil.ItemCallback<CategoryUiState>() {
    override fun areItemsTheSame(
        old: CategoryUiState,
        aNew: CategoryUiState
    ): Boolean {
        return old.name == aNew.name
    }

    override fun areContentsTheSame(
        old: CategoryUiState,
        aNew: CategoryUiState
    ): Boolean {
        return old == aNew
    }
}