package com.fiz.mono.calculator.ui

import com.fiz.mono.calculator.domain.models.CalculatorMemory

data class CalculatorUiState(
    val calculatorMemory: CalculatorMemory = CalculatorMemory(),
    val history: String = ""
)

val CalculatorUiState.result get() = calculatorMemory.getResult()