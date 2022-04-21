package com.fiz.mono.ui.models

import com.fiz.mono.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.database.entity.TransactionEntity
import org.threeten.bp.LocalDate

data class TransactionUiState(
    val id: Int,
    val localDate: LocalDate,
    var value: Double,
    val nameCategory: String,
    val note: String,
    val imgSrc: Int,
    val photo: List<String?> = mutableListOf()
) {
    suspend fun toTransactionEntity(): TransactionEntity {
        return TransactionEntity(
            id = this.id,
            localDate = this.localDate,
            value = this.value,
            nameCategory = this.nameCategory,
            note = this.note,
            mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(this.imgSrc),
            photoPaths = this.photo
        )
    }

    companion object {

        suspend fun fromTransactionEntity(transactionEntity: TransactionEntity): TransactionUiState {
            return TransactionUiState(
                id = transactionEntity.id,
                localDate = transactionEntity.localDate,
                value = transactionEntity.value,
                nameCategory = transactionEntity.nameCategory,
                note = transactionEntity.note,
                imgSrc = CategoryIconUiStateDataSource().getDrawableCategoryIcon(transactionEntity.mapImgSrc),
                photo = transactionEntity.photoPaths
            )
        }
    }
}
