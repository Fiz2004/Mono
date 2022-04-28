package com.fiz.mono.database.mapper

import com.fiz.mono.database.data_source.CategoryIconsLocalDataSourceImpl
import com.fiz.mono.database.entity.TransactionEntity
import com.fiz.mono.domain.models.Transaction

suspend fun Transaction.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        localDate = localDate,
        value = value,
        nameCategory = nameCategory,
        note = note,
        mapImgSrc = CategoryIconsLocalDataSourceImpl().getIDCategoryIcon(imgSrc),
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
        imgSrc = CategoryIconsLocalDataSourceImpl().getDrawableCategoryIcon(mapImgSrc),
        photo = photoPaths
    )
}

