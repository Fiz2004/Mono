package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.CategoryIcon
import com.fiz.mono.domain.repositories.CategoryIconsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveAllCategoryIconsUseCase @Inject constructor(
    private val categoryIconsRepository: CategoryIconsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    operator fun invoke(): Flow<List<CategoryIcon>> {
        return categoryIconsRepository.allCategoryIcons
            .flowOn(defaultDispatcher)
    }

}