package com.fiz.mono.domain.use_case

import com.fiz.mono.domain.repositories.CategoryRepository
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
            if (type == "expense") {
                categoryRepository.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryRepository.insertNewCategoryIncome(name, selectedIcon)
            }
        }
}