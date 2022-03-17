package com.fiz.mono.ui.calculator

import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    private var number1: String = ""
    private var number2: String = ""
    private var currentOperator: String = ""
    private var lastOperation = "="
    private var history = ""

    fun resetData() {
        number1 = ""
        number2 = ""
        currentOperator = ""
        lastOperation = "="
        history = ""
    }

    fun getCurrentOperation(): String {
        return number1 + currentOperator + number2
    }

    fun deleteLastSymbol() {
        if (currentOperator == "") {
            number1 = number1.substring(0, number1.length - 1)
        } else {
            number2 = number2.substring(0, number2.length - 1)
        }
    }

    fun numberClick(symbol: String) {
        if (currentOperator == "") {
            if (symbol != "." || !number1.contains("."))
                number1 += symbol
        } else {
            if (symbol != "." || !number2.contains("."))
                number2 += symbol
        }
        if (lastOperation == "=" && currentOperator != "") {
            currentOperator = ""
        }
    }

    fun operatorClick(operator: String) {
        currentOperator = operator
        if (operator == "=") {
            try {
                performOperation()
            } catch (ex: NumberFormatException) {
                history = ""
            }
            currentOperator = ""
        }
        lastOperation = currentOperator
    }

    private fun performOperation() {
        var result = ""
        when (lastOperation) {
            "/" -> result = if (number2 == "0") {
                number1 = result
                number2 = ""
                ""
            } else {
                (number1.toDouble() / number2.toDouble()).toString()
            }
            "x" -> result = (number1.toDouble() * number2.toDouble()).toString()
            "+" -> result = (number1.toDouble() + number2.toDouble()).toString()
            "-" -> result = (number1.toDouble() - number2.toDouble()).toString()
        }
        if (result.length > 8)
            result = result.substring(8)
        if (result.toDouble() % 1.0 == 0.0)
            result = result.toDouble().toInt().toString()
        history = number1 + lastOperation + number2
        number1 = result
        number2 = ""
    }

    fun getHistory(): String {
        return history
    }
}