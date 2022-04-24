package com.fiz.mono.feature_category_add.domain.use_case

import com.fiz.mono.core.domain.use_case.ObserveAllCategoriesExpenseUseCase
import com.fiz.mono.core.domain.use_case.ObserveAllCategoriesIncomeUseCase
import javax.inject.Inject

class CategoryAddUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val observeAllCategoryIconsUseCase: ObserveAllCategoryIconsUseCase,
    val selectOneItemCategoryIconUseCase: SelectOneItemCategoryIconUseCase,
    val insertNewCategoryUseCase: InsertNewCategoryUseCase
)