package com.fiz.mono.database.mapper

import com.fiz.mono.database.data_source.CategoryIconsLocalDataSourceImpl
import com.fiz.mono.database.entity.CategoryEntity
import com.fiz.mono.domain.models.Category

suspend fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        mapImgSrc = CategoryIconsLocalDataSourceImpl().getIDCategoryIcon(imgSrc),
    )
}

suspend fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        imgSrc = CategoryIconsLocalDataSourceImpl().getDrawableCategoryIcon(mapImgSrc),
        selected = false
    )
}