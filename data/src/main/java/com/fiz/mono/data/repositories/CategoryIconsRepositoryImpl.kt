package com.fiz.mono.data.repositories

import com.fiz.mono.data.data_source.getIdIconsCategoriesByResourceDrawable
import com.fiz.mono.data.data_source.getResourceDrawableByIdIconsCategories
import com.fiz.mono.domain.models.CategoryIcon
import com.fiz.mono.domain.repositories.CategoryIconsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryIconsRepositoryImpl @Inject constructor() :
    CategoryIconsRepository {
    override var mapIconsCategories: List<CategoryIcon> =
        com.fiz.mono.data.data_source.mapIconsCategories

    override suspend fun getDrawableCategoryIcon(id: String): Int =
        getResourceDrawableByIdIconsCategories(id)

    override suspend fun getIDCategoryIcon(imgSrc: Int): String =
        getIdIconsCategoriesByResourceDrawable(imgSrc)
}