package com.fiz.mono.data.mapper

import com.fiz.mono.data.entity.IconCategoryEntity
import com.fiz.mono.domain.models.CategoryIcon

fun IconCategoryEntity.toCategoryIcon(): CategoryIcon {
    return CategoryIcon(
        id = id,
        imgSrc = imgSrc,
        selected = false
    )
}
