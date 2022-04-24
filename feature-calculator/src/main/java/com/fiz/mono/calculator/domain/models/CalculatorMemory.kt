package com.fiz.mono.calculator.domain.models

data class CalculatorMemory(
    val number1: String = "",
    val number2: String = "",
    val currentOperator: String = "",
    val lastOperation: String = "",
) {

    fun getResult(): String {
        return number1 + currentOperator + number2
    }

    fun deleteLastSymbol(): CalculatorMemory {
        var number1 = number1
        if (currentOperator == "")
            number1 = number1.dropLast(1)

        var number2 = number2
        if (currentOperator != "")
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
            }

        var currentOperator = currentOperator
        if (currentOperator != "")
            if (this.number2.isBlank()) {
                currentOperator = ""
            }

        return copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator
        )
    }

    fun addNumber(newNumber: String): CalculatorMemory {
        var number1 = number1
        if (currentOperator == "" &&
            (newNumber != "." || !number1.contains("."))
        )
            number1 += newNumber

        var number2 = number2
        if (currentOperator != "" &&
            (newNumber != "." || !number2.contains("."))
        )
            number2 += newNumber

        var currentOperator = currentOperator
        if (lastOperation == "=" && currentOperator != "")
            currentOperator = ""

        return copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator
        )
    }

    fun doOperator(operator: String): CalculatorMemory {
        var number1 = number1
        var number2 = number2
        var lastOperation = currentOperator
        var currentOperator = operator

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

        return copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator,
            lastOperation = lastOperation
        )
    }

    fun getHistory(operator: String): String {
        var number1 = number1
        var number2 = number2
        var lastOperation = currentOperator
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

    fun reset(): CalculatorMemory {
        return CalculatorMemory()
    }
}