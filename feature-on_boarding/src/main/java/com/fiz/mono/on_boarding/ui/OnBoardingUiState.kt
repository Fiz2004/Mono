package com.fiz.mono.on_boarding.ui

sealed class OnBoardingUiEvent {
    object ClickNextPages : OnBoardingUiEvent()
    object ClickSkipButton : OnBoardingUiEvent()
    object ClickBackPress : OnBoardingUiEvent()
}

data class OnBoardingUiState(
    val page: Int = 0
){
    companion object{
        const val MAX_PAGE=3
    }
}