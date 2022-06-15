package com.fiz.mono.on_boarding.ui

data class OnBoardingViewState(
    val page: Int = 0
){
    companion object{
        const val MAX_PAGE=3
    }
}