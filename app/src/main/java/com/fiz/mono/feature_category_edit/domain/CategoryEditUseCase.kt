package com.fiz.mono.feature_category_edit.domain

import com.fiz.mono.core.domain.use_case.ObserveAllCategoriesExpenseUseCase
import com.fiz.mono.core.domain.use_case.ObserveAllCategoriesIncomeUseCase
import javax.inject.Inject

class CategoryEditUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val deleteCategoryItemUseCase: DeleteCategoryItemUseCase,
    val selectItemForTwoListByLastItemClickUseCase: SelectItemForTwoListByLastItemClickUseCase,
)