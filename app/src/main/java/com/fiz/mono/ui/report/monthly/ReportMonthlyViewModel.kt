package com.fiz.mono.ui.report.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportMonthlyViewModel @Inject constructor(private val transactionDataSource: TransactionDataSource) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ReportMonthlyUiState())
    val uiState: StateFlow<ReportMonthlyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            transactionDataSource.allTransactions.collect { allTransactions ->
                _uiState.update {
                    it.copy(
                        allTransactions = allTransactions
                    )
                }
            }
        }
    }

    fun setTabSelectedReport(tabSelectedReport: Int) {
        _uiState.update {
            it.copy(tabSelectedReport = tabSelectedReport)
        }
    }

    fun setDate(date: LocalDate) {
        _uiState.update {
            it.copy(
                date = date,
                isDateChange = true
            )
        }
    }

    fun init(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    fun onDataChange() {
        _uiState.update {
            it.copy(
                isDateChange = false
            )
        }
    }
}
