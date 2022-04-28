package com.fiz.mono.domain.use_case

import javax.inject.Inject

class CategoryAddUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val observeAllCategoryIconsUseCase: ObserveAllCategoryIconsUseCase,
    val selectOneItemCategoryIconUseCase: SelectOneItemCategoryIconUseCase,
    val insertNewCategoryUseCase: InsertNewCategoryUseCase
)