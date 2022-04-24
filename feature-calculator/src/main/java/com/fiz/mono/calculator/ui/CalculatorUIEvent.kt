package com.fiz.mono.calculator.ui

sealed class CalculatorUIEvent {
    data class ClickNumber(val number: String) : CalculatorUIEvent()
    class ClickOperator(val operator: String) : CalculatorUIEvent()
    object ClickReset : CalculatorUIEvent()
    object ClickAC : CalculatorUIEvent()
    object ClickDelete : CalculatorUIEvent()
}