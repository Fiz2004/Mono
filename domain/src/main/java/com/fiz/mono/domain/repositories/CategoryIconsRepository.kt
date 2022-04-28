package com.fiz.mono.domain.repositories

import com.fiz.mono.domain.models.CategoryIcon
import kotlinx.coroutines.flow.Flow

interface CategoryIconsRepository {
    var allCategoryIcons: Flow<List<CategoryIcon>>

    suspend fun getDrawableCategoryIcon(id: String): Int

    suspend fun getIDCategoryIcon(imgSrc: Int): String
}