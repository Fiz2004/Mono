package com.fiz.mono.feature_calculator.domain.use_case

import com.fiz.mono.feature_calculator.domain.models.CalculatorMemory
import javax.inject.Inject

class AddOperatorUseCase @Inject constructor() {
    operator fun invoke(calculatorMemory: CalculatorMemory, operator: String): CalculatorMemory {
        var number1 = calculatorMemory.number1
        var number2 = calculatorMemory.number2
        var currentOperator = operator
        var lastOperation = calculatorMemory.lastOperation
        var history = calculatorMemory.history
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
        return calculatorMemory.copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator,
            lastOperation = currentOperator,
            history = history,
        )
    }
}