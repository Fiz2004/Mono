package com.fiz.mono.feature_calculator.domain.models

data class CalculatorMemory(
    val number1: String = "",
    val number2: String = "",
    val currentOperator: String = "",
    val lastOperation: String = "",
    val history: String = ""
)