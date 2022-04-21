package com.fiz.mono.ui.models

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.database.entity.CategoryEntity

data class CategoryUiState(
    val id: String,
    val name: String,
    val imgSrc: Int,
    val selected: Boolean = false
) {
    suspend fun toCategoryEntity(): CategoryEntity {
        return CategoryEntity(
            id = this.id,
            name = this.name,
            mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(this.imgSrc),
        )
    }

    companion object {

        suspend fun fromCategoryEntity(categoryEntity: CategoryEntity): CategoryUiState {
            return CategoryUiState(
                id = categoryEntity.id,
                name = categoryEntity.name,
                imgSrc = CategoryIconUiStateDataSource().getDrawableCategoryIcon(categoryEntity.mapImgSrc),
                selected = false
            )
        }
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