package com.fiz.mono.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.ui.models.TransactionUiState
import org.threeten.bp.LocalDate

@Entity
data class TransactionEntity(
    @PrimaryKey
    val id: Int,
    val localDate: LocalDate,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val mapImgSrc: String,
    val photoPaths: List<String?> = mutableListOf()
) {
    suspend fun toTransactionUiState(): TransactionUiState {
        return TransactionUiState(
            id = this.id,
            localDate = this.localDate,
            value = this.value,
            nameCategory = this.nameCategory,
            note = this.note,
            imgSrc = CategoryIconUiStateDataSource().getDrawableCategoryIcon(this.mapImgSrc),
            photo = this.photoPaths
        )
    }
}