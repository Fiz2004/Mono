package com.fiz.mono.calculator.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() :
    ViewModel() {

    var uiState = MutableStateFlow(CalculatorUiState()); private set

    fun onEvent(event: CalculatorUIEvent) {
        when (event) {
            CalculatorUIEvent.ClickAC -> resetData()

            CalculatorUIEvent.ClickReset -> resetData()

            is CalculatorUIEvent.ClickNumber -> numberClick(event.number)

            is CalculatorUIEvent.ClickOperator -> operatorClick(event.operator)

            CalculatorUIEvent.ClickDelete -> deleteClick()
        }
    }

    private fun resetData() {
        val calculatorMemory = uiState.value.calculatorMemory
        val newCalculatorMemory = calculatorMemory.reset()

        uiState.value = uiState.value
            .copy(calculatorMemory = newCalculatorMemory)
    }

    private fun deleteClick() {
        if (uiState.value.result.isBlank())
            return

        val calculatorMemory = uiState.value.calculatorMemory
        val newCalculatorMemory = calculatorMemory.deleteLastSymbol()

        uiState.value = uiState.value
            .copy(calculatorMemory = newCalculatorMemory)
    }

    private fun numberClick(newNumber: String) {
        val calculatorMemory = uiState.value.calculatorMemory
        val newCalculatorMemory = calculatorMemory.addNumber(newNumber)

        uiState.value = uiState.value
            .copy(calculatorMemory = newCalculatorMemory)
    }

    private fun operatorClick(operator: String) {
        val calculatorMemory = uiState.value.calculatorMemory
        val history = calculatorMemory.getHistory(operator)
        val newCalculatorMemory = calculatorMemory.doOperator(operator)

        uiState.value = uiState.value
            .copy(
                calculatorMemory = newCalculatorMemory,
                history = history
            )
    }
}