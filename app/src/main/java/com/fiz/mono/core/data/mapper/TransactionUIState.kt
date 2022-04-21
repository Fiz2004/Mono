package com.fiz.mono.core.data.mapper

import com.fiz.mono.core.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.core.domain.models.Transaction
import com.fiz.mono.database.entity.TransactionEntity

suspend fun Transaction.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        localDate = localDate,
        value = value,
        nameCategory = nameCategory,
        note = note,
        mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(imgSrc),
        photoPaths = photo
    )
}

suspend fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        localDate = localDate,
        value = value,
        nameCategory = nameCategory,
        note = note,
        imgSrc = CategoryIconUiStateDataSource().getDrawableCategoryIcon(mapImgSrc),
        photo = photoPaths
    )
}

