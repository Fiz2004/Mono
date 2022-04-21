package com.fiz.mono.feature_calculator.domain.use_case

import javax.inject.Inject

data class CalculatorUseCase @Inject constructor(
    val addNumberUseCase: AddNumberUseCase,
    val addOperatorUseCase: AddOperatorUseCase,
    val deleteLastSymbolUseCase: DeleteLastSymbolUseCase,
)