package com.fiz.mono.category_add.domain

import com.fiz.mono.domain.use_case.ObserveAllCategoriesExpenseUseCase
import com.fiz.mono.domain.use_case.ObserveAllCategoriesIncomeUseCase
import com.fiz.mono.domain.use_case.ObserveAllCategoryIconsUseCase
import com.fiz.mono.domain.use_case.SelectOneItemCategoryIconUseCase
import javax.inject.Inject

class CategoryAddUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val observeAllCategoryIconsUseCase: ObserveAllCategoryIconsUseCase,
    val selectOneItemCategoryIconUseCase: SelectOneItemCategoryIconUseCase,
    val insertNewCategoryUseCase: InsertNewCategoryUseCase
)