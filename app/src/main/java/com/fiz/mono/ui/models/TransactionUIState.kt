package com.fiz.mono.ui.models

import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.data.entity.Transaction
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
    suspend fun toTransaction(): Transaction {
        return Transaction(
            id = this.id,
            date = this.date,
            value = this.value,
            nameCategory = this.nameCategory,
            note = this.note,
            mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(this.imgSrc),
            photo = this.photo
        )
    }
}
