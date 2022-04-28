package com.fiz.mono.database.mapper

import com.fiz.mono.database.entity.CategoryIconEntity
import com.fiz.mono.domain.models.CategoryIcon

fun CategoryIconEntity.toCategoryIcon(): CategoryIcon {
    return CategoryIcon(
        id = id,
        imgSrc = imgSrc,
        selected = false
    )
}
