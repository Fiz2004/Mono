package com.fiz.mono.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val transactionRepository: TransactionRepository
) :
    ViewModel() {

    var uiState = MutableStateFlow(CalendarUiState()); private set

    var navigationUiState = MutableStateFlow(CalendarNavigationState()); private set

    fun getFormatDate(pattern: String): String {
        return DateTimeFormatter.ofPattern(pattern).format(uiState.value.date)
    }

    fun setMonth(month: Int) {
        val newDate = uiState.value.date.withMonth(month)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun setDate(day: Int) {
        if (day == 0) return

        val newDate = uiState.value.date.withDayOfMonth(day)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateDayPlusOne() {
        val newDate = uiState.value.date.plusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    private fun dateDayMinusOne() {
        val newDate = uiState.value.date.minusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateMonthPlusOne() {
        val newDate = uiState.value.date.plusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateMonthMinusOne() {
        val newDate = uiState.value.date.minusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }


    init {
        viewModelScope.launch(Dispatchers.Default) {

            transactionRepository.allTransactions.collect { allTransactions ->

                uiState.value = uiState.value.copy(
                    allTransactions = allTransactions,
                    isAllTransactionsLoaded = true
                )

            }

        }

    }

    init {
        viewModelScope.launch {
            settingsLocalDataSource.stateFlow.collect {

                uiState.value = uiState.value
                    .copy(currency = it.currency)
            }
        }
    }

    fun clickBackButton() {
        navigationUiState.value = navigationUiState.value.copy(isReturn = true)
    }

    fun returned() {
        navigationUiState.value = navigationUiState.value.copy(isReturn = false)
    }

    fun changeData(date: LocalDate) {
        uiState.value = uiState.value.copy(date = date, isDateChange = true)
    }

    fun onChangeDate() {
        uiState.value = uiState.value.copy(isDateChange = false)
    }

    fun onAllTransactionsLoaded() {
        uiState.value = uiState.value.copy(isAllTransactionsLoaded = false)
    }
}