package com.fiz.mono.category_add.domain

import com.fiz.mono.domain.models.TypeTransaction
import com.fiz.mono.domain.repositories.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertNewCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    suspend operator fun invoke(type: TypeTransaction, name: String, selectedIcon: String) =
        withContext(defaultDispatcher) {
            if (type == TypeTransaction.Expense) {
                categoryRepository.insertNewCategoryExpense(name, selectedIcon)
            } else {
                categoryRepository.insertNewCategoryIncome(name, selectedIcon)
            }
        }
}