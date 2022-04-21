package com.fiz.mono.core.data.mapper

import com.fiz.mono.core.data.data_source.CategoryIconUiStateDataSource
import com.fiz.mono.core.domain.models.Category
import com.fiz.mono.database.entity.CategoryEntity

suspend fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        mapImgSrc = CategoryIconUiStateDataSource().getIDCategoryIcon(imgSrc),
    )
}

suspend fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        imgSrc = CategoryIconUiStateDataSource().getDrawableCategoryIcon(mapImgSrc),
        selected = false
    )
}
