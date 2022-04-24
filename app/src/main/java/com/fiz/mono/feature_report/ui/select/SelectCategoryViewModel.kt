package com.fiz.mono.feature_report.ui.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.core.domain.repositories.CategoryRepository
import com.fiz.mono.feature_report.ui.select.SelectCategoryFragment.Companion.TYPE_EXPENSE
import com.fiz.mono.feature_report.ui.select.SelectCategoryFragment.Companion.TYPE_INCOME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(categoryRepository: CategoryRepository) :
    ViewModel() {
    var uiState = MutableStateFlow(SelectUiState()); private set
    var navigationUiState = MutableStateFlow(SelectNavigationUiState()); private set

    init {

        categoryRepository.allCategoryExpense
            .onEach { allCategoryExpense ->
                uiState.value = uiState.value
                    .copy(allCategoryExpense = allCategoryExpense)
            }.launchIn(viewModelScope)

        categoryRepository.allCategoryIncome
            .onEach { allCategoryIncome ->
                uiState.value = uiState.value
                    .copy(allCategoryIncome = allCategoryIncome)
            }.launchIn(viewModelScope)
    }

    fun clickExpenseRecyclerView(position: Int) {
        val id = uiState.value.allCategoryExpense[position].id

        navigationUiState.value = navigationUiState.value
            .copy(isMove = true, id = id, type = TYPE_EXPENSE)
    }

    fun clickIncomeRecyclerView(position: Int) {
        val id = uiState.value.allCategoryIncome[position].id

        navigationUiState.value = navigationUiState.value
            .copy(isMove = true, id = id, type = TYPE_INCOME)
    }

    fun moved() {
        navigationUiState.value = navigationUiState.value
            .copy(isMove = false)
    }
}