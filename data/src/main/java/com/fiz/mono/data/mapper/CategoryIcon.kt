package com.fiz.mono.data.mapper

import com.fiz.mono.data.entity.CategoryIconEntity
import com.fiz.mono.domain.models.CategoryIcon

fun CategoryIconEntity.toCategoryIcon(): CategoryIcon {
    return CategoryIcon(
        id = id,
        imgSrc = imgSrc,
        selected = false
    )
}
