package com.fiz.mono.database.repositories

import com.fiz.mono.database.data_source.CategoryIconsLocalDataSourceImpl
import com.fiz.mono.domain.models.CategoryIcon
import com.fiz.mono.domain.repositories.CategoryIconsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryIconsRepositoryImpl @Inject constructor(private val CategoryIconsLocalDataSourceImpl: CategoryIconsLocalDataSourceImpl) :
    CategoryIconsRepository {
    override var allCategoryIcons: Flow<List<CategoryIcon>> =
        CategoryIconsLocalDataSourceImpl.allCategoryIcons

    override suspend fun getDrawableCategoryIcon(id: String): Int =
        CategoryIconsLocalDataSourceImpl.getDrawableCategoryIcon(id)

    override suspend fun getIDCategoryIcon(imgSrc: Int): String =
        CategoryIconsLocalDataSourceImpl.getIDCategoryIcon(imgSrc)
}