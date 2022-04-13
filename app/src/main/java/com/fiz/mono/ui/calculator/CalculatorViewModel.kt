package com.fiz.mono.ui.calculator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun resetData() {
        _uiState.update {
            CalculatorUiState()
        }
    }

    fun deleteClick() {
        _uiState.update {
            it.deleteLastSymbol()
        }
    }

    fun numberClick(symbol: String) {
        _uiState.update {
            it.addNumber(symbol)
        }
    }

    fun operatorClick(operator: String) {
        _uiState.update {
            it.addOperator(operator)
        }
    }
}