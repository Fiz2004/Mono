package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val transactionDataSource: TransactionDataSource) :
    ViewModel() {
    var uiState = MutableStateFlow(CalendarUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.Default) {
            transactionDataSource.allTransactions.collect { allTransactions ->
                uiState.update {
                    it.copy(
                        allTransactions = allTransactions,
                        isAllTransactionsLoaded = true
                    )
                }
            }
        }
    }

    fun clickBackButton() {
        uiState.update {
            it.copy(isReturn = true)
        }
    }

    fun changeData(date: LocalDate) {
        uiState.update {
            it.copy(date = date, isDateChange = true)
        }
    }

    fun onChangeDate() {
        uiState.update {
            it.copy(isDateChange = false)
        }
    }

    fun onAllTransactionsLoaded() {
        uiState.update {
            it.copy(isAllTransactionsLoaded = false)
        }
    }
}