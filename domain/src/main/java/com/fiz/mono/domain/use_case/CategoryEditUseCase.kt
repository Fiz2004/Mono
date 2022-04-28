package com.fiz.mono.domain.use_case

import javax.inject.Inject

class CategoryEditUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val deleteCategoryItemUseCase: DeleteCategoryItemUseCase,
    val selectItemForTwoListByLastItemClickUseCase: SelectItemForTwoListByLastItemClickUseCase,
)