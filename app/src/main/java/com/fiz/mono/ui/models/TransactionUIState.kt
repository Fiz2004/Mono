package com.fiz.mono.ui.models

import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.data.entity.TransactionEntity
import java.util.*

data class TransactionUiState(
    val id: Int,
    val date: Date,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val imgSrc: Int,
    val photo: List<String?> = mutableListOf()
) {
    suspend fun toTransaction(): TransactionEntity {
        return TransactionEntity(
            id = this.id,
            date = this.date,
            value = this.value,
            nameCategory = this.nameCategory,
            note = this.note,
            mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(this.imgSrc),
            photoPaths = this.photo
        )
    }
}
