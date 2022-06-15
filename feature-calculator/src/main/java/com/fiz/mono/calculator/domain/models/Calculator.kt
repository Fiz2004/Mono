package com.fiz.mono.calculator.domain.models

class Calculator(
    var number1: String = "",
    var number2: String = "",
    var currentOperator: String = "",
    var lastOperation: String = "",
) {

    fun getResult(): String {
        return number1 + currentOperator + number2
    }

    fun deleteLastSymbol() {
        if (currentOperator == "")
            number1 = number1.dropLast(1)

        if (currentOperator != "")
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
            }

        if (currentOperator != "")
            if (this.number2.isBlank()) {
                currentOperator = ""
            }
    }

    fun addNumber(newNumber: String) {
        if (currentOperator == "" &&
            (newNumber != "." || !number1.contains("."))
        )
            number1 += newNumber

        if (currentOperator != "" &&
            (newNumber != "." || !number2.contains("."))
        )
            number2 += newNumber

        if (lastOperation == "=" && currentOperator != "")
            currentOperator = ""
    }

    fun doOperator(operator: String) {
        lastOperation = currentOperator
        currentOperator = operator

        if (operator == "=") {
            try {
                val n1: Double = number1.toDouble()
                val n2: Double = number2.toDouble()
                var resultOperation = when (lastOperation) {
                    "/" -> if (n2 == 0.0) {
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
                number1 = if (resultOperation % 1.0 == 0.0)
                    String.format("%.0f", resultOperation)
                else
                    String.format("%.2f", resultOperation)

                number2 = ""

                lastOperation = "="
            } catch (ex: NumberFormatException) {
            }

            currentOperator = ""
        }
    }

    fun getHistory(operator: String): String {
        lastOperation = currentOperator
        var history = ""
        if (operator == "=") {
            try {
                when (lastOperation) {
                    "/" -> if (number2.toDouble() == 0.0) {
                        number1 = ""
                        number2 = ""
                        lastOperation = ""
                    }
                }
                history = number1 + lastOperation + number2
            } catch (ex: NumberFormatException) {
                history = ""
            }
        }

        return history
    }

    fun reset(): Calculator {
        return Calculator()
    }
}