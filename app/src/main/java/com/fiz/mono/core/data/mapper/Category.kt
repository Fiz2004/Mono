package com.fiz.mono.core.data.mapper

import com.fiz.mono.core.data.data_source.CategoryIconsLocalDataSource
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.database.entity.CategoryEntity

suspend fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        mapImgSrc = CategoryIconsLocalDataSource().getIDCategoryIcon(imgSrc),
    )
}

suspend fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        imgSrc = CategoryIconsLocalDataSource().getDrawableCategoryIcon(mapImgSrc),
        selected = false
    )
}
