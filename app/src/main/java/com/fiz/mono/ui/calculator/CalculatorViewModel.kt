package com.fiz.mono.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val number1 = MutableLiveData("")

    private val number2 = MutableLiveData("")

    private val currentOperator = MutableLiveData("")

    private val lastOperation = MutableLiveData("")

    private val _history = MutableLiveData("")
    val history: LiveData<String> = _history

    val result: MediatorLiveData<String> = MediatorLiveData()

    init {
        result.addSource(number1) {
            result.value = number1.value + currentOperator.value + number2.value
        }
        result.addSource(number2) {
            result.value = number1.value + currentOperator.value + number2.value
        }
        result.addSource(currentOperator) {
            result.value = number1.value + currentOperator.value + number2.value
        }
    }

    fun resetData() {
        number1.value = ""
        number2.value = ""
        currentOperator.value = ""
        lastOperation.value = "="
        _history.value = ""
    }

    fun deleteLastSymbol() {
        if (currentOperator.value == "") {
            number1.value = number1.value?.substring(0, number1.value!!.length - 1)
        } else {
            number2.value = number2.value?.substring(0, number2.value!!.length - 1)
        }
    }

    fun numberClick(symbol: String) {
        if (currentOperator.value == "") {
            if (symbol != "." || !number1.value!!.contains("."))
                number1.value += symbol
        } else {
            if (symbol != "." || !number2.value!!.contains("."))
                number2.value += symbol
        }
        if (lastOperation.value == "=" && currentOperator.value != "") {
            currentOperator.value = ""
        }
    }

    fun operatorClick(operator: String) {
        currentOperator.value = operator
        if (operator == "=") {
            try {
                performOperation()
            } catch (ex: NumberFormatException) {
                _history.value = ""
            }
            currentOperator.value = ""
        }
        lastOperation.value = currentOperator.value
    }

    private fun performOperation() {
        val n1: Double = number1.value?.toDouble() ?: 0.0
        val n2: Double = number2.value?.toDouble() ?: 0.0

        var resultOperation = when (lastOperation.value) {
            "/" -> if (n2 == 0.0) {
                number1.value = ""
                number2.value = ""
                lastOperation.value = ""
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

        result.value = if (resultOperation % 1.0 == 0.0)
            String.format("%.0f", resultOperation)
        else
            String.format("%.2f", resultOperation)

        _history.value = number1.value + lastOperation.value + number2.value
        number1.value = result.value
        number2.value = ""
    }
}