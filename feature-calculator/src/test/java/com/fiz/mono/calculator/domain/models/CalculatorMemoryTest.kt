package com.fiz.mono.calculator.domain.models

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorMemoryTest {

    private lateinit var calculatorMemory: CalculatorMemory

    @Before
    fun setUp() {
        calculatorMemory = CalculatorMemory()
    }

    @Test
    fun getResult() {
        val newCalculatorMemory =
            CalculatorMemory(number1 = "23", number2 = "12", currentOperator = "+")

        assertEquals("23+12", newCalculatorMemory.getResult())
    }

    @Test
    fun deleteLastSymbolForNumber1() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .deleteLastSymbol()

        assertEquals("52", newCalculatorMemory.number1)
    }

    @Test
    fun deleteLastSymbolForNumber2() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .deleteLastSymbol()

        assertEquals("5", newCalculatorMemory.number2)
    }

    @Test
    fun deleteLastSymbolForCurrentOperator() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .deleteLastSymbol()

        assertEquals("", newCalculatorMemory.currentOperator)
    }

    @Test
    fun addNumberForNumber1() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")


        assertEquals("521", newCalculatorMemory.number1)
    }

    @Test
    fun addNumberForNumber2() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")


        assertEquals("521", newCalculatorMemory.number2)
    }

    @Test
    fun addNumberForNumber1AfterOperation() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .doOperator("=")
            .addNumber("5")


        assertEquals(
            CalculatorMemory(
                number1 = "5225",
                number2 = "",
                currentOperator = "",
                lastOperation = "="
            ), newCalculatorMemory
        )
    }

    @Test
    fun doOperatorIfOperatorPlus() {
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .doOperator("=")

        assertEquals(
            CalculatorMemory(
                number1 = "522",
                number2 = "",
                currentOperator = "",
                lastOperation = "="
            ), newCalculatorMemory
        )
    }

    @Test
    fun getHistory() {
        val history = calculatorMemory
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
        val newCalculatorMemory = calculatorMemory
            .addNumber("1")
            .doOperator("+")
            .addNumber("5")
            .addNumber("2")
            .addNumber("1")
            .reset()

        assertEquals(CalculatorMemory(), newCalculatorMemory)
    }
}