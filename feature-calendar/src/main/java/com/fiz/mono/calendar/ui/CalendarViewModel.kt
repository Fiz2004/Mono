package com.fiz.mono.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val
    transactionRepository: TransactionRepository
) :
    ViewModel() {

    var uiState = MutableStateFlow(CalendarUiState()); private set

    var navigationUiState = MutableStateFlow(CalendarNavigationState()); private set

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