package com.fiz.mono.domain.use_case

import android.content.Context
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.repositories.CategoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAllCategoriesIncomeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.allCategoryIncome.map {
            it + Category(
                "i",
                context.getString(com.fiz.mono.common.ui.resources.R.string.add_more),
                0
            )
        }.flowOn(defaultDispatcher)
    }
}