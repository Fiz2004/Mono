package com.fiz.mono.feature_calculator.domain.use_case

import com.fiz.mono.feature_calculator.domain.models.CalculatorMemory
import javax.inject.Inject

class DeleteLastSymbolUseCase @Inject constructor(){
    operator fun invoke(calculatorMemory: CalculatorMemory): CalculatorMemory {
        var number1 = calculatorMemory.number1
        if (calculatorMemory.currentOperator == "")
            number1 = number1.dropLast(1)

        var number2 = number1
        if (calculatorMemory.currentOperator != "")
            number2 = number2.dropLast(1)

        return calculatorMemory.copy(number1 = number1, number2 = number2)
    }

}