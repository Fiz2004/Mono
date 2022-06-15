package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.CategoryIcon

interface CategoryIconsRepository {
    var mapIconsCategories: List<CategoryIcon>

    suspend fun getDrawableCategoryIcon(id: String): Int

    suspend fun getIDCategoryIcon(imgSrc: Int): String
}