package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class CalendarViewModel(private val transactionDataSource: TransactionDataSource) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            transactionDataSource.allTransactions.collect { allTransactions ->
                _uiState.update {
                    it.copy(
                        allTransactions = allTransactions,
                    )
                }
            }
        }
    }

    fun clickBackButton() {
        _uiState.update {
            it.copy(isReturn = true)
        }
    }

    fun changeData(date: LocalDate) {
        _uiState.update {
            it.copy(date = date, isDateChange = true)
        }
    }

    fun onChangeDate() {
        _uiState.update {
            it.copy(isDateChange = false)
        }
    }
}