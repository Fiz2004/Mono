package com.fiz.mono.feature_report.ui.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.feature_report.domain.use_case.ReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportMonthlyViewModel @Inject constructor(
    reportUseCase: ReportUseCase
) :
    ViewModel() {
    var uiState = MutableStateFlow(ReportMonthlyUiState()); private set

    init {

        reportUseCase.observeAllTransactionsUseCase()
            .onEach { allTransactions ->
                uiState.value = uiState.value
                    .copy(allTransactions = allTransactions)
            }.launchIn(viewModelScope)

        reportUseCase.getCurrentBalanceUseCase(uiState.value.currency)
            .onEach { currentBalance ->
                uiState.value = uiState.value
                    .copy(currentBalance = currentBalance)
            }.launchIn(viewModelScope)

        val date = uiState.mapLatest { uiState -> uiState.date }

        date.flatMapLatest { date ->
            reportUseCase.getCurrentIncomeUseCase(uiState.value.currency, date)
        }.onEach { currentIncome ->
            uiState.value = uiState.value
                .copy(currentIncome = currentIncome)
        }.launchIn(viewModelScope)

        date.flatMapLatest { date ->
            reportUseCase.getCurrentExpenseUseCase(uiState.value.currency, date)
        }.onEach { currentExpense ->
            uiState.value = uiState.value
                .copy(currentExpense = currentExpense)
        }.launchIn(viewModelScope)

        date.flatMapLatest { date ->
            reportUseCase.getBalanceForMonthUseCase(uiState.value.currency, date)
        }.onEach { currentExpenseIncome ->
            uiState.value = uiState.value
                .copy(currentExpenseIncome = currentExpenseIncome)
        }.launchIn(viewModelScope)

        date.flatMapLatest { date ->
            reportUseCase.getLastBalanceForMonthUseCase(uiState.value.currency, date)
        }.onEach { lastBalance ->
            uiState.value = uiState.value
                .copy(lastBalance = lastBalance)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: ReportMonthlyUiEvent) {
        when (event) {
            is ReportMonthlyUiEvent.ObserveData -> setDate(event.date)
            is ReportMonthlyUiEvent.ClickTransactionsFilter -> setTabSelectedReport(event.filter)
        }
    }

    private fun setTabSelectedReport(tabSelectedReport: Int) {
        uiState.value = uiState.value
            .copy(tabSelectedReport = tabSelectedReport)
    }

    private fun setDate(date: LocalDate) {
        uiState.value = uiState.value
            .copy(
                date = date,
                isDateChange = true
            )
    }

    fun start(currency: String) {
        uiState.value = uiState.value
            .copy(currency = currency)
    }

    fun dataChanged() {
        uiState.value = uiState.value
            .copy(isDateChange = false)
    }
}
