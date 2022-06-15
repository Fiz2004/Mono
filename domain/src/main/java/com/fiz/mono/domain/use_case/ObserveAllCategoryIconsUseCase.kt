package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.CategoryIcon
import com.fiz.mono.domain.repositories.CategoryIconsRepository
import javax.inject.Inject

class ObserveAllCategoryIconsUseCase @Inject constructor(
    private val categoryIconsRepository: CategoryIconsRepository
) {

    operator fun invoke(): List<CategoryIcon> {
        return categoryIconsRepository.mapIconsCategories
    }

}