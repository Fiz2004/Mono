package com.fiz.mono.category_edit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.use_case.CategoryEditUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val categoryEditUseCase: CategoryEditUseCase,
) : ViewModel() {

    var uiState = MutableStateFlow(CategoryEditUiState()); private set

    var navigationUiState = MutableStateFlow(CategoryEditNavigationUIState()); private set

    init {
        observeAllCategoriesExpense()
        observeAllCategoriesIncome()
    }

    private fun observeAllCategoriesExpense() {
        categoryEditUseCase.observeAllCategoriesExpenseUseCase()
            .onEach { list ->
                uiState.value = uiState.value.copy(
                    allCategoryExpense = list
                )
            }.launchIn(viewModelScope)
    }

    private fun observeAllCategoriesIncome() {
        categoryEditUseCase.observeAllCategoriesIncomeUseCase()
            .onEach { list ->
                uiState.value = uiState.value.copy(
                    allCategoryIncome = list
                )
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: CategoryEditUiEvent) {
        when (event) {
            is CategoryEditUiEvent.ClickExpenseItem -> clickExpenseRecyclerView(event.position)
            is CategoryEditUiEvent.ClickIncomeItem -> clickIncomeRecyclerView(event.position)

            CategoryEditUiEvent.ClickBackButton -> clickBackButton()
            CategoryEditUiEvent.ClickRemoveButton -> removeSelectItem()
        }
    }

    private fun clickExpenseRecyclerView(position: Int) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(
                allCategoryExpense = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getNewClickList(
                    uiState.value.allCategoryExpense, position
                ),
                allCategoryIncome = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getOtherList(
                    uiState.value.allCategoryExpense, uiState.value.allCategoryIncome, position
                ),
                type = TYPE_EXPENSE
            )

            if (position == uiState.value.allCategoryExpense.size - 1) {
                navigationUiState.value = navigationUiState.value.copy(
                    isMoveAdd = true
                )
            }
        }
    }

    private fun clickIncomeRecyclerView(position: Int) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(
                allCategoryIncome = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getNewClickList(
                    uiState.value.allCategoryIncome, position
                ),
                allCategoryExpense = categoryEditUseCase.selectItemForTwoListByLastItemClickUseCase.getOtherList(
                    uiState.value.allCategoryIncome, uiState.value.allCategoryExpense, position
                ),
                type = TYPE_INCOME
            )

            if (position == uiState.value.allCategoryIncome.size - 1) {
                navigationUiState.value = navigationUiState.value.copy(
                    isMoveAdd = true
                )
            }
        }
    }

    private fun removeSelectItem() {
        viewModelScope.launch {
            categoryEditUseCase.deleteCategoryItemUseCase(
                uiState.value.allCategoryExpense,
                uiState.value.allCategoryIncome
            )

        }
    }

    private fun clickBackButton() {
        navigationUiState.value = navigationUiState.value.copy(
            isReturn = true
        )
    }

    fun getType(): String {
        return if (uiState.value.type == TYPE_EXPENSE)
            TYPE_EXPENSE
        else
            TYPE_INCOME
    }

    fun movedAdd() {
        navigationUiState.value = navigationUiState.value.copy(
            isMoveAdd = false
        )
    }

    fun returned() {
        navigationUiState.value = navigationUiState.value.copy(
            isReturn = false
        )
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}