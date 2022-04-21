package com.fiz.mono.feature_calculator.ui

import com.fiz.mono.feature_calculator.domain.models.CalculatorMemory

data class CalculatorUiState(
    val calculatorMemory: CalculatorMemory = CalculatorMemory(),
    val result: String = calculatorMemory.number1 + calculatorMemory.currentOperator + calculatorMemory.number2,
    val history: String = ""
)