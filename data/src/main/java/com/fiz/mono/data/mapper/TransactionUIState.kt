package com.fiz.mono.data.mapper

import com.fiz.mono.data.data_source.getIdIconsCategoriesByResourceDrawable
import com.fiz.mono.data.data_source.getResourceDrawableByIdIconsCategories
import com.fiz.mono.data.entity.TransactionEntity
import com.fiz.mono.domain.models.Transaction

fun Transaction.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        localDate = localDate,
        value = value,
        nameCategory = nameCategory,
        note = note,
        mapImgSrc = getIdIconsCategoriesByResourceDrawable(imgSrc),
        photoPaths = photo
    )
}

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        localDate = localDate,
        value = value,
        nameCategory = nameCategory,
        note = note,
        imgSrc = getResourceDrawableByIdIconsCategories(mapImgSrc),
        photo = photoPaths
    )
}

