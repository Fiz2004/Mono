package com.fiz.mono.report.ui.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.use_case.ObserveAllTransactionsUseCase
import com.fiz.mono.report.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportMonthlyViewModel @Inject constructor(
    private val getTransactionsForMonthUseCase: GetTransactionsForMonthUseCase,
    private val settingsRepository: SettingsRepository,
    private val observeAllTransactionsUseCase: ObserveAllTransactionsUseCase,
    private val getCurrentBalanceUseCase: GetCurrentBalanceUseCase,
    private val getCurrentIncomeUseCase: GetCurrentIncomeUseCase,
    private val getCurrentExpenseUseCase: GetCurrentExpenseUseCase,
    private val getBalanceForMonthUseCase: GetBalanceForMonthUseCase,
    private val getLastBalanceForMonthUseCase: GetLastBalanceForMonthUseCase,
) :
    ViewModel() {
    var viewState = MutableStateFlow(ReportMonthlyViewState())
        private set

    init {
        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

        settingsRepository.observeDate()
            .onEach { date ->
                viewState.value = viewState.value
                    .copy(date = date)
            }.launchIn(viewModelScope)
    }

    init {

        val dateFlow = viewState.mapLatest { uiState -> uiState.date }

        dateFlow.flatMapLatest { date ->
            observeAllTransactionsUseCase()
        }.onEach { allTransactions ->
            val transactionsForMonth = getTransactionsForMonthUseCase(
                allTransactions,
                viewState.value.date,
                viewState.value.tabSelectedReport
            )
            viewState.value = viewState.value
                .copy(transactionsForMonth = transactionsForMonth)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            getCurrentBalanceUseCase(viewState.value.currency)
        }.onEach { currentBalance ->
            viewState.value = viewState.value
                .copy(currentBalance = currentBalance)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            getCurrentIncomeUseCase(viewState.value.currency, date)
        }.onEach { currentIncome ->
            viewState.value = viewState.value
                .copy(currentIncome = currentIncome)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            getCurrentExpenseUseCase(viewState.value.currency, date)
        }.onEach { currentExpense ->
            viewState.value = viewState.value
                .copy(currentExpense = currentExpense)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            getBalanceForMonthUseCase(viewState.value.currency, date)
        }.onEach { currentExpenseIncome ->
            viewState.value = viewState.value
                .copy(currentExpenseIncome = currentExpenseIncome)
        }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            getLastBalanceForMonthUseCase(viewState.value.currency, date)
        }.onEach { lastBalance ->
            viewState.value = viewState.value
                .copy(lastBalance = lastBalance)
        }.launchIn(viewModelScope)

    }

    fun onEvent(event: ReportMonthlyEvent) {
        when (event) {
            ReportMonthlyEvent.DateLeftClicked -> dateLeftClicked()
            ReportMonthlyEvent.DateRightClicked -> dateRightClicked()
            is ReportMonthlyEvent.TransactionsFilterClicked -> transactionsFilterClicked(event.filter)
        }
    }

    private fun dateLeftClicked() {
        viewModelScope.launch {
            val date = settingsRepository.getDate()

            val newDate = date.minusMonths(1)

            settingsRepository.setDate(newDate)
        }
    }

    private fun dateRightClicked() {
        viewModelScope.launch {
            val date = settingsRepository.getDate()

            val newDate = date.plusMonths(1)

            settingsRepository.setDate(newDate)
        }
    }

    private fun transactionsFilterClicked(tabSelectedReport: Int) {
        viewState.value = viewState.value
            .copy(tabSelectedReport = tabSelectedReport)
    }
}

