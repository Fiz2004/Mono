package com.fiz.mono.report.ui.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import com.fiz.mono.domain.use_case.ReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportMonthlyViewModel @Inject constructor(
    private val getTransactionsForMonth: GetTransactionsForMonth,
    private val settingsLocalDataSource: SettingsLocalDataSource,
    reportUseCase: ReportUseCase
) :
    ViewModel() {
    var uiState = MutableStateFlow(ReportMonthlyUiState()); private set

    init {

        reportUseCase.observeAllTransactionsUseCase()
            .onEach { allTransactions ->
                val transactionsForMonth = getTransactionsForMonth(
                    allTransactions,
                    uiState.value.date,
                    uiState.value.tabSelectedReport
                )
                uiState.value = uiState.value
                    .copy(transactionsForMonth = transactionsForMonth)
            }.launchIn(viewModelScope)

        reportUseCase.getCurrentBalanceUseCase(uiState.value.currency)
            .onEach { currentBalance ->
                uiState.value = uiState.value
                    .copy(currentBalance = currentBalance)
            }.launchIn(viewModelScope)

        val dateFlow = uiState.mapLatest { uiState -> uiState.date }

        dateFlow.flatMapLatest { date ->
            reportUseCase.getCurrentIncomeUseCase(uiState.value.currency, date)
        }.onEach { currentIncome ->
            uiState.value = uiState.value
                .copy(currentIncome = currentIncome)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            reportUseCase.getCurrentExpenseUseCase(uiState.value.currency, date)
        }.onEach { currentExpense ->
            uiState.value = uiState.value
                .copy(currentExpense = currentExpense)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            reportUseCase.getBalanceForMonthUseCase(uiState.value.currency, date)
        }.onEach { currentExpenseIncome ->
            uiState.value = uiState.value
                .copy(currentExpenseIncome = currentExpenseIncome)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            reportUseCase.getLastBalanceForMonthUseCase(uiState.value.currency, date)
        }.onEach { lastBalance ->
            uiState.value = uiState.value
                .copy(lastBalance = lastBalance)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            settingsLocalDataSource.stateFlow.collect {

                uiState.value = uiState.value
                    .copy(currency = it.currency)
            }
        }
    }

    fun onEvent(event: ReportMonthlyUiEvent) {
        when (event) {
            is ReportMonthlyUiEvent.ClickTransactionsFilter -> setTabSelectedReport(event.filter)
            ReportMonthlyUiEvent.ClickDateLeft -> dateMonthMinusOne()
            ReportMonthlyUiEvent.ClickDateRight -> dateMonthPlusOne()
        }
    }


    private fun dateMonthPlusOne() {
        val newDate = uiState.value.date.plusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    private fun dateMonthMinusOne() {
        val newDate = uiState.value.date.minusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    private fun setTabSelectedReport(tabSelectedReport: Int) {
        uiState.value = uiState.value
            .copy(tabSelectedReport = tabSelectedReport)
    }
}

