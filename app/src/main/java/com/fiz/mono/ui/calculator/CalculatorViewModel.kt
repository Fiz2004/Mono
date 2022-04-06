package com.fiz.mono.ui.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val number1: String = "",
    val number2: String = "",
    val currentOperator: String = "",
    val lastOperation: String = "",
    val history: String = ""
) {
    val result: String
        get() = number1 + currentOperator + number2

    fun deleteLastSymbol(): CalculatorUiState {
        val number1 = if (currentOperator == "")
            number1.dropLast(1) else number1
        val number2 = if (currentOperator != "")
            number2.dropLast(1) else number2
        return copy(number1 = number1, number2 = number2)
    }

    fun addNumber(symbol: String): CalculatorUiState {
        val number1 = if (currentOperator == "")
            if (symbol != "." || !number1.contains("."))
                number1 + symbol else number1
        else number1


        val number2 = if (currentOperator != "")
            if (symbol != "." || !number2.contains("."))
                number2 + symbol else number2
        else number2

        val currentOperator = if (lastOperation == "=" && currentOperator != "")
            "" else currentOperator

        return copy(number1 = number1, number2 = number2, currentOperator = currentOperator)
    }

    fun addOperator(operator: String): CalculatorUiState {
        var current = copy()
        current = current.copy(currentOperator = operator)
        if (operator == "=") {
            try {
                current = current.performOperation()
            } catch (ex: NumberFormatException) {
                current = current.copy(history = "")
            }
            current = current.copy(currentOperator = "")
        }
        current = current.copy(lastOperation = current.currentOperator)
        return current
    }


    private fun performOperation(): CalculatorUiState {
        var current = copy()
        val n1: Double = number1.toDouble()
        val n2: Double = number2.toDouble()

        var resultOperation = when (lastOperation) {
            "/" -> if (n2 == 0.0) {
                current = current.copy(number1 = "")
                current = current.copy(number2 = "")
                current = current.copy(lastOperation = "")
                0.0
            } else {
                n1 / n2
            }
            "x" -> n1 * n2
            "+" -> n1 + n2
            "-" -> n1 - n2
            else -> 0.0
        }

        if (resultOperation > 99999999) {
            resultOperation = 100000000.0
        }

        current = current.copy(history = number1 + lastOperation + number2)
        current = current.copy(
            number1 = if (resultOperation % 1.0 == 0.0)
                String.format("%.0f", resultOperation)
            else
                String.format("%.2f", resultOperation)
        )
        current = current.copy(number2 = "")
        return current
    }
}

class CalculatorViewModel : ViewModel() {
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