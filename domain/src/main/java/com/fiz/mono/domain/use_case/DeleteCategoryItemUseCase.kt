package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.repositories.CategoryRepository
import javax.inject.Inject

class DeleteCategoryItemUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(
        allCategoryExpense: List<Category>,
        allCategoryIncome: List<Category>
    ) {
        allCategoryExpense
            .find { it.selected }
            ?.let {
                categoryRepository.removeCategory(it)
            }

        allCategoryIncome
            .find { it.selected }
            ?.let {
                categoryRepository.removeCategory(it)
            }
    }
}