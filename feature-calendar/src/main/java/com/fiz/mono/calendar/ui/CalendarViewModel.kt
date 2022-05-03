package com.fiz.mono.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.models.TransactionsDataItem
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import com.fiz.mono.domain.use_case.getAllTransactionsForDay
import com.fiz.mono.domain.use_case.getTransactionsForDaysCurrentMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var uiState = MutableStateFlow(CalendarUiState()); private set

    var navigationUiState = MutableStateFlow(CalendarNavigationState()); private set

    init {

        val dateFlow = uiState.mapLatest { uiState -> uiState.date }

        settingsRepository.currency.load()
            .onEach { currency ->
                uiState.value = uiState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

        dateFlow.flatMapLatest { date ->
            transactionRepository.allTransactions
        }.onEach { allTransactions ->
            uiState.value = uiState.value
                .copy(
                    isAllTransactionsLoaded = true,
                    calendarDataItem = CalendarDataItem.getListCalendarDataItem(
                        getTransactionsForDaysCurrentMonth(allTransactions, uiState.value.date)
                    ),
                    transactionsDataItem = TransactionsDataItem.getListTransactionsDataItem(
                        getAllTransactionsForDay(allTransactions, uiState.value.date)
                    )
                )
        }.launchIn(viewModelScope)

    }

    fun setMonth(month: Int) {
        val newDate = uiState.value.date.withMonth(month)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsRepository.date.save(newDate)
        }
    }

    fun setDate(day: Int) {
        if (day == 0) return

        val newDate = uiState.value.date.withDayOfMonth(day)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsRepository.date.save(newDate)
        }
    }

    fun clickBackButton() {
        navigationUiState.value = navigationUiState.value
            .copy(isReturn = true)
    }

    fun returned() {
        navigationUiState.value = navigationUiState.value
            .copy(isReturn = false)
    }

    fun changeData(date: LocalDate) {
        uiState.value = uiState.value
            .copy(date = date)
    }

    fun onAllTransactionsLoaded() {
        uiState.value = uiState.value
            .copy(isAllTransactionsLoaded = false)
    }
}