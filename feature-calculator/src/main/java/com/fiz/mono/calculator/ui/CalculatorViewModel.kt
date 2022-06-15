package com.fiz.mono.calculator.ui

import androidx.lifecycle.ViewModel
import com.fiz.mono.calculator.domain.models.Calculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() :
    ViewModel() {

    private val calculator: Calculator = Calculator()

    var viewState = MutableStateFlow(CalculatorViewState())
        private set

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            CalculatorEvent.ACClicked -> resetClicked()
            CalculatorEvent.ResetClicked -> resetClicked()
            is CalculatorEvent.NumberClicked -> numberClicked(event.number)
            is CalculatorEvent.OperatorClicked -> operatorClicked(event.operator)
            CalculatorEvent.DeleteClicked -> deleteClicked()
        }
    }

    private fun resetClicked() {
        calculator.reset()

        viewState.value = viewState.value
            .copy(result = calculator.getResult())
    }

    private fun deleteClicked() {
        if (calculator.getResult().isBlank())
            return

        calculator.deleteLastSymbol()

        viewState.value = viewState.value
            .copy(result = calculator.getResult())
    }

    private fun numberClicked(newNumber: String) {
        calculator.addNumber(newNumber)

        viewState.value = viewState.value
            .copy(result = calculator.getResult())
    }

    private fun operatorClicked(operator: String) {
        val history = calculator.getHistory(operator)
        calculator.doOperator(operator)

        viewState.value = viewState.value
            .copy(
                result = calculator.getResult(),
                history = history
            )
    }
}