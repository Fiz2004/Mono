package com.fiz.mono.report.ui.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.models.TypeTransaction
import com.fiz.mono.domain.repositories.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(categoryRepository: CategoryRepository) :
    ViewModel() {
    var viewState = MutableStateFlow(SelectViewState())
        private set
    var viewEffects = MutableSharedFlow<SelectViewEffect>()
        private set

    init {

        categoryRepository.observeCategoriesExpense
            .onEach { allCategoryExpense ->
                viewState.value = viewState.value
                    .copy(allCategoryExpense = allCategoryExpense)
            }.launchIn(viewModelScope)

        categoryRepository.observeCategoriesIncome
            .onEach { allCategoryIncome ->
                viewState.value = viewState.value
                    .copy(allCategoryIncome = allCategoryIncome)
            }.launchIn(viewModelScope)
    }

    fun clickExpenseRecyclerView(position: Int) {
        viewModelScope.launch {
            val id = viewState.value.allCategoryExpense[position].id
            viewEffects.emit(
                SelectViewEffect.MoveCategoryReport(
                    id = id,
                    type = TypeTransaction.Expense
                )
            )
        }
    }

    fun clickIncomeRecyclerView(position: Int) {
        viewModelScope.launch {
            val id = viewState.value.allCategoryIncome[position].id
            viewEffects.emit(
                SelectViewEffect.MoveCategoryReport(
                    id = id,
                    type = TypeTransaction.Income
                )
            )
        }
    }
}