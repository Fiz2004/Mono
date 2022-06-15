package com.fiz.mono.calculator.domain.models

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorMemoryTest {

    private lateinit var calculator: Calculator

    @Before
    fun setUp() {
        calculator = Calculator()
    }

    @Test
    fun getResult() {
        val newCalculator =
            Calculator(number1 = "23", number2 = "12", currentOperator = "+")

        assertEquals("23+12", newCalculator.getResult())
    }

    @Test
    fun deleteLastSymbolForNumber1() {
        val newCalculatorMemory = calculator
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .deleteLastSymbol()

        assertEquals("52", newCalculatorMemory.number1)
    }

    @Test
    fun deleteLastSymbolForNumber2() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .deleteLastSymbol()

        assertEquals("5", newCalculatorMemory.number2)
    }

    @Test
    fun deleteLastSymbolForCurrentOperator() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .deleteLastSymbol()

        assertEquals("", newCalculatorMemory.currentOperator)
    }

    @Test
    fun addNumberForNumber1() {
        val newCalculatorMemory = calculator
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")


        assertEquals("521", newCalculatorMemory.number1)
    }

    @Test
    fun addNumberForNumber2() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")


        assertEquals("521", newCalculatorMemory.number2)
    }

    @Test
    fun addNumberForNumber1AfterOperation() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .doOperator("=")
            .addNumber("5")


        assertEquals(
            Calculator(
                number1 = "5225",
                number2 = "",
                currentOperator = "",
                lastOperation = "="
            ), newCalculatorMemory
        )
    }

    @Test
    fun doOperatorIfOperatorPlus() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .doOperator("=")

        assertEquals(
            Calculator(
                number1 = "522",
                number2 = "",
                currentOperator = "",
                lastOperation = "="
            ), newCalculatorMemory
        )
    }

    @Test
    fun getHistory() {
        val history = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .getHistory("=")

        assertEquals("1+521", history)
    }

    @Test
    fun reset() {
        val newCalculatorMemory = calculator
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .reset()

        assertEquals(Calculator(), newCalculatorMemory)
    }
}