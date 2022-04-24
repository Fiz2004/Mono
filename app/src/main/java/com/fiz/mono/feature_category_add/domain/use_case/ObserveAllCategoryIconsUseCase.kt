package com.fiz.mono.feature_category_add.domain.use_case

import com.fiz.mono.core.data.data_source.CategoryIconsLocalDataSource
import com.fiz.mono.core.domain.models.CategoryIcon
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveAllCategoryIconsUseCase @Inject constructor(
    private val categoryIconsLocalDataSource: CategoryIconsLocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    operator fun invoke(): Flow<List<CategoryIcon>> {
        return categoryIconsLocalDataSource.allCategoryIcons
            .flowOn(defaultDispatcher)
    }

}