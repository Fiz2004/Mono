package com.fiz.mono.input.ui

sealed class InputNavigationEvent {
    object MoveReturn : InputNavigationEvent()
    object MoveEdit : InputNavigationEvent()
    object MoveCalendar : InputNavigationEvent()
    object MoveOnBoarding : InputNavigationEvent()
    object MovePinPassword : InputNavigationEvent()
}