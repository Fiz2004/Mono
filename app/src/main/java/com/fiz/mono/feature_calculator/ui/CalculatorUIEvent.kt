package com.fiz.mono.feature_calculator.ui

sealed class CalculatorUIEvent {
    data class ClickNumber(val number: String) : CalculatorUIEvent()
    class ClickOperator(val operator: String) : CalculatorUIEvent()
    object ClickReset : CalculatorUIEvent()
    object ClickAC : CalculatorUIEvent()
    object ClickDelete : CalculatorUIEvent()
}