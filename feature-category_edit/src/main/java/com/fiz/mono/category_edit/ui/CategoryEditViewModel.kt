package com.fiz.mono.category_edit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.category_edit.domain.CategoryEditUseCase
import com.fiz.mono.domain.models.TypeTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val categoryEditUseCase: CategoryEditUseCase,
) : ViewModel() {

    var viewState = MutableStateFlow(CategoryEditViewState())
        private set

    var viewEffects = MutableSharedFlow<CategoryEditViewEffect>()
        private set

    init {
        observeAllCategoriesExpense()
        observeAllCategoriesIncome()
    }

    private fun observeAllCategoriesExpense() {
        categoryEditUseCase.observeAllCategoriesExpenseUseCase()
            .onEach { list ->
                viewState.value = viewState.value
                    .copy(allCategoryExpense = list)
            }.launchIn(viewModelScope)
    }

    private fun observeAllCategoriesIncome() {
        categoryEditUseCase.observeAllCategoriesIncomeUseCase()
            .onEach { list ->
                viewState.value = viewState.value
                    .copy(allCategoryIncome = list)
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: CategoryEditEvent) {
        when (event) {
            CategoryEditEvent.BackButtonClicked -> backButtonClicked()
            CategoryEditEvent.RemoveButtonClicked -> removeButtonClicked()
            is CategoryEditEvent.ExpenseItemClicked -> expenseItemClicked(event.position)
            is CategoryEditEvent.IncomeItemClicked -> incomeItemClicked(event.position)
        }
    }

    private fun expenseItemClicked(position: Int) {
        viewModelScope.launch {
            viewState.value = viewState.value
                .copy(
                    allCategoryExpense = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getNewClickList(
                        viewState.value.allCategoryExpense, position
                    ),
                    allCategoryIncome = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getOtherList(
                        viewState.value.allCategoryExpense,
                        viewState.value.allCategoryIncome,
                        position
                    ),
                    type = TypeTransaction.Expense
                )

            if (position == viewState.value.allCategoryExpense.size - 1) {
                viewEffects.emit(CategoryEditViewEffect.MoveCategoryAdd)
            }
        }
    }

    private fun incomeItemClicked(position: Int) {
        viewModelScope.launch {
            viewState.value = viewState.value
                .copy(
                    allCategoryIncome = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getNewClickList(
                        viewState.value.allCategoryIncome, position
                    ),
                    allCategoryExpense = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getOtherList(
                        viewState.value.allCategoryIncome,
                        viewState.value.allCategoryExpense,
                        position
                    ),
                    type = TypeTransaction.Income
                )

            if (position == viewState.value.allCategoryIncome.size - 1) {
                viewEffects.emit(CategoryEditViewEffect.MoveCategoryAdd)
            }
        }
    }

    private fun removeButtonClicked() {
        viewModelScope.launch {
            categoryEditUseCase.deleteCategoryItemUseCase(
                viewState.value.allCategoryExpense,
                viewState.value.allCategoryIncome
            )

        }
    }

    private fun backButtonClicked() {
        viewModelScope.launch {
            viewEffects.emit(CategoryEditViewEffect.MoveReturn)
        }
    }

    fun getType(): TypeTransaction {
        return viewState.value.type
    }

}