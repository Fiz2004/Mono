package com.fiz.mono.feature_calculator.ui

import androidx.lifecycle.ViewModel
import com.fiz.mono.feature_calculator.domain.use_case.CalculatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(private val calculatorUseCase: CalculatorUseCase) :
    ViewModel() {
    var uiState = MutableStateFlow(CalculatorUiState()); private set

    fun onEvent(event: CalculatorUIEvent) {
        when (event) {
            CalculatorUIEvent.ClickAC -> resetData()
            CalculatorUIEvent.ClickDelete -> deleteClick()
            is CalculatorUIEvent.ClickNumber -> numberClick(event.number)
            is CalculatorUIEvent.ClickOperator -> operatorClick(event.operator)
            CalculatorUIEvent.ClickReset -> resetData()
        }
    }

    private fun resetData() {
        uiState.value = CalculatorUiState()
    }

    private fun deleteClick() {

        if (uiState.value.result.isBlank())
            return

        val calculatorMemory = calculatorUseCase.deleteLastSymbolUseCase(uiState.value.calculatorMemory)

        uiState.value =
            uiState.value.copy(calculatorMemory = calculatorMemory)
    }

    private fun numberClick(symbol: String) {

        val calculatorMemory = calculatorUseCase.addNumberUseCase(
            uiState.value.calculatorMemory,
            symbol
        )

        uiState.value =
            uiState.value.copy(calculatorMemory = calculatorMemory)
    }

    private fun operatorClick(operator: String) {

        val calculatorMemory = calculatorUseCase.addOperatorUseCase(
            uiState.value.calculatorMemory,
            operator
        )

        uiState.value =
            uiState.value.copy(
                calculatorMemory = calculatorMemory
            )
    }
}