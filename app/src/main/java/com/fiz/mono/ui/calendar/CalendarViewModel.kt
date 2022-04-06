package com.fiz.mono.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.entity.Transaction
import com.fiz.mono.ui.shared_adapters.TransactionsDataItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class CalendarUiState(
    val date: Calendar = Calendar.getInstance(),
    val allTransactions: List<Transaction> = listOf(),
    val isReturn: Boolean = false,
    val calendarDataItem: List<CalendarDataItem> = listOf(),
    val transactionsDataItem: List<TransactionsDataItem> = listOf(),
)

class CalendarViewModel(private val transactionDataSource: TransactionDataSource) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            transactionDataSource.allTransactions.collect { allTransactions ->
                _uiState.update {
                    it.copy(
                        allTransactions = allTransactions,
                        calendarDataItem = CalendarDataItem.getListCalendarDataItem(
                            transactionDataSource.getTransactionsForDaysCurrentMonth(
                                uiState.value.date
                            )
                        ),
                        transactionsDataItem = TransactionsDataItem.getListTransactionsDataItem(
                            transactionDataSource.getAllTransactionsForDay(
                                uiState.value.date
                            )
                        )

                    )
                }
            }
        }
    }

    private fun getListCalendarDataItem() {
        viewModelScope.launch {
            val transactionsForDaysCurrentMonth =
                transactionDataSource.getTransactionsForDaysCurrentMonth(uiState.value.date)
            _uiState.update {
                it.copy(calendarDataItem = CalendarDataItem.getListCalendarDataItem(transactionsForDaysCurrentMonth))
            }
        }
    }

    private fun getListTransactionsDataItem() {
        viewModelScope.launch {
            val allTransactionsForDay = transactionDataSource.getAllTransactionsForDay(uiState.value.date)
            _uiState.update {
                it.copy(transactionsDataItem = TransactionsDataItem.getListTransactionsDataItem(allTransactionsForDay))
            }
        }
    }

    fun clickBackButton() {
        _uiState.update {
            it.copy(isReturn = true)
        }
    }

    fun changeData(date: Calendar) {
        _uiState.update {
            it.copy(date = date)
        }
//        getListCalendarDataItem()
//        getListTransactionsDataItem()
    }
}