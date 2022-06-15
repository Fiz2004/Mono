package com.fiz.mono.on_boarding.ui

sealed class OnBoardingEvent {
    object NextPagesClicked : OnBoardingEvent()
    object SkipButtonClicked : OnBoardingEvent()
    object BackPressClicked : OnBoardingEvent()
}