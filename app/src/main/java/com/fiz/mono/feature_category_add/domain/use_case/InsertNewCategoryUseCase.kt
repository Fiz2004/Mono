package com.fiz.mono.feature_category_add.domain.use_case

import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.feature_category_edit.ui.CategoryEditViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertNewCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    suspend operator fun invoke(type: String, name: String, selectedIcon: String) =
        withContext(defaultDispatcher) {
            if (type == CategoryEditViewModel.TYPE_EXPENSE) {
                categoryRepository.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryRepository.insertNewCategoryIncome(name, selectedIcon)
            }
        }
}