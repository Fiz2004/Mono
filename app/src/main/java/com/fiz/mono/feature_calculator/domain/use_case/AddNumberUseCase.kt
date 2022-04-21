package com.fiz.mono.feature_calculator.domain.use_case

import com.fiz.mono.feature_calculator.domain.models.CalculatorMemory
import javax.inject.Inject

class AddNumberUseCase @Inject constructor() {
    operator fun invoke(calculatorMemory: CalculatorMemory, symbol: String): CalculatorMemory {
        var number1 = calculatorMemory.number1
        if (calculatorMemory.currentOperator == "")
            if (symbol != "." || !number1.contains(".")) {
                number1 += symbol
            }

        var number2 = calculatorMemory.number2
        if (calculatorMemory.currentOperator != "")
            if (symbol != "." || !number2.contains("."))
                number2 += symbol

        var currentOperator = calculatorMemory.currentOperator
        if (calculatorMemory.lastOperation == "=" && currentOperator != "")
            currentOperator = ""

        return calculatorMemory.copy(
            number1 = number1,
            number2 = number2,
            currentOperator = currentOperator
        )
    }
}