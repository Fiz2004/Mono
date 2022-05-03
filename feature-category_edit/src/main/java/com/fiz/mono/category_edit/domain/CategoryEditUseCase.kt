package com.fiz.mono.category_edit.domain

import com.fiz.mono.domain.use_case.DeleteCategoryItemUseCase
import com.fiz.mono.domain.use_case.ObserveAllCategoriesExpenseUseCase
import com.fiz.mono.domain.use_case.ObserveAllCategoriesIncomeUseCase
import com.fiz.mono.domain.use_case.SelectItemForTwoListByLastItemClickUseCase
import javax.inject.Inject

class CategoryEditUseCase @Inject constructor(
    val observeAllCategoriesExpenseUseCase: ObserveAllCategoriesExpenseUseCase,
    val observeAllCategoriesIncomeUseCase: ObserveAllCategoriesIncomeUseCase,
    val deleteCategoryItemUseCase: DeleteCategoryItemUseCase,
    val selectItemForTwoListByLastItemClickUseCase: SelectItemForTwoListByLastItemClickUseCase,
)