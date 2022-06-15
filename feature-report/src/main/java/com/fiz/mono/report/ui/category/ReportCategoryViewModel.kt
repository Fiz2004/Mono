package com.fiz.mono.report.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.models.TypeTransaction
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReportCategoryViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    var viewState = MutableStateFlow(ReportCategoryViewState())
        private set

    init {

        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

    }

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

        transactionRepository.observeTransactions
            .onEach { allTransactions ->
                viewState.value = viewState.value
                    .copy(allTransactions = allTransactions)
            }.launchIn(viewModelScope)

    }

    fun clickMonthToggleButton() {
        if (viewState.value.reportFor == PeriodForReport.Month) return

        viewState.value = viewState.value
            .copy(reportFor = PeriodForReport.Month)

    }

    fun clickWeekToggleButton() {
        if (viewState.value.reportFor == PeriodForReport.Week) return

        viewState.value = viewState.value
            .copy(reportFor = PeriodForReport.Week)
    }

    fun onGraphImageViewLayoutChange() {
        viewState.value = viewState.value
            .copy(isCanGraph = true)
    }

    fun start(type: TypeTransaction, id: String) {
        viewState.value = viewState.value
            .copy(
                isExpense = type == TypeTransaction.Expense,
                id = id
            )
    }

}