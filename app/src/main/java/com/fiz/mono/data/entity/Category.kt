package com.fiz.mono.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fiz.mono.data.data_source.CategoryIconDataSource
import com.fiz.mono.ui.models.CategoryUiState

@Entity
data class Category(
    @PrimaryKey
    val id: String,
    val name: String,
    val mapImgSrc: String
) {
    fun toCategoryUiState(): CategoryUiState {
        return CategoryUiState(
            id = this.id,
            name = this.name,
            imgSrc = CategoryIconDataSource().getDrawableCategoryIcon(this.mapImgSrc),
            selected = false
        )
    }
}