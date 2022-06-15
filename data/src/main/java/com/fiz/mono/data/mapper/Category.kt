package com.fiz.mono.data.mapper

import com.fiz.mono.data.data_source.getIdIconsCategoriesByResourceDrawable
import com.fiz.mono.data.data_source.getResourceDrawableByIdIconsCategories
import com.fiz.mono.data.entity.CategoryEntity
import com.fiz.mono.domain.models.Category

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        mapImgSrc = getIdIconsCategoriesByResourceDrawable(imgSrc),
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        imgSrc = getResourceDrawableByIdIconsCategories(mapImgSrc),
        selected = false
    )
}
