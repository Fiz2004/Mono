package com.fiz.mono.ui.category_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.fiz.mono.data.repositories.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryEditViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategoryExpenseForEdit()
                .distinctUntilChanged()
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            allCategoryExpense = list
                        )
                    }
                }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategoryIncomeForEdit()
                .distinctUntilChanged()
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            allCategoryIncome = list
                        )
                    }
                }
        }
    }

    fun clickExpenseRecyclerView(position: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.clickExpenseRecyclerView(position)
            }
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.clickIncomeRecyclerView(position)
            }
        }
    }

    fun removeSelectItem() {
        viewModelScope.launch {
            uiState.value.allCategoryExpense
                .find { it.selected }
                ?.let {
                    categoryRepository.removeCategoryExpense(it)
                }

            uiState.value.allCategoryIncome
                .find { it.selected }
                ?.let {
                    categoryRepository.removeCategoryIncome(it)
                }
        }
    }

    fun clickBackButton() {
        _uiState.update {
            it.copy(
                isReturn = true
            )
        }
    }

    fun getActionForMoveAdd(): NavDirections {
        return if (uiState.value.type == TYPE_EXPENSE)
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_EXPENSE)
        else
            CategoryEditFragmentDirections
                .actionCategoryFragmentToCategoryAddFragment(TYPE_INCOME)
    }

    fun moveAdd() {
        _uiState.update {
            it.copy(
                isMoveAdd = false
            )
        }
    }

    companion object {
        const val TYPE_EXPENSE = "expense"
        const val TYPE_INCOME = "income"
    }
}