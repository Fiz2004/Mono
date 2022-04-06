package com.fiz.mono.ui.calculator

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
        var number1 = number1
        if (currentOperator == "")
            number1 = number1.dropLast(1)

        var number2 = number1
        if (currentOperator != "")
            number2 = number2.dropLast(1)

        return copy(number1 = number1, number2 = number2)
    }

    fun addNumber(symbol: String): CalculatorUiState {
        var number1 = number1
        if (currentOperator == "")
            if (symbol != "." || !number1.contains(".")) {
                number1 += symbol
            }

        var number2 = number2
        if (currentOperator != "")
            if (symbol != "." || !number2.contains("."))
                number2 += symbol

        var currentOperator = currentOperator
        if (lastOperation == "=" && currentOperator != "")
            currentOperator = ""

        return copy(number1 = number1, number2 = number2, currentOperator = currentOperator)
    }

    fun addOperator(operator: String): CalculatorUiState {
        var number1 = number1
        var number2 = number2
        var currentOperator = operator
        var lastOperation = lastOperation
        var history = history
        if (operator == "=") {
            try {
                val n1: Double = number1.toDouble()
                val n2: Double = number2.toDouble()
                var resultOperation = when (lastOperation) {
                    "/" -> if (n2 == 0.0) {
                        number1 = ""
                        number2 = ""
                        lastOperation = ""
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
                history = number1 + lastOperation + number2
                number1 = if (resultOperation % 1.0 == 0.0)
                    String.format("%.0f", resultOperation)
                else
                    String.format("%.2f", resultOperation)

                number2 = ""
            } catch (ex: NumberFormatException) {
                history = ""
            }
            currentOperator = ""
        }
        return copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator,
            lastOperation = currentOperator,
            history = history,
        )
    }


}