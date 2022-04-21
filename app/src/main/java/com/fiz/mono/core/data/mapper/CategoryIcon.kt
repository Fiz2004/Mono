package com.fiz.mono.core.data.mapper

import com.fiz.mono.core.domain.models.CategoryIcon
import com.fiz.mono.database.entity.CategoryIconEntity

fun CategoryIconEntity.toCategoryIcon(): CategoryIcon {
    return CategoryIcon(
        id = id,
        imgSrc = imgSrc,
        selected = false
    )
}
