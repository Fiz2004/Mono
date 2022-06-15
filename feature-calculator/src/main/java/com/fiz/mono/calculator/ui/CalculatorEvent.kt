package com.fiz.mono.calculator.ui

sealed class CalculatorEvent {
    data class NumberClicked(val number: String) : CalculatorEvent()
    class OperatorClicked(val operator: String) : CalculatorEvent()
    object ResetClicked : CalculatorEvent()
    object ACClicked : CalculatorEvent()
    object DeleteClicked : CalculatorEvent()
}