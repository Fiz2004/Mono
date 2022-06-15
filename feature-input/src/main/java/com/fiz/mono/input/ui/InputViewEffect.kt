package com.fiz.mono.input.ui

sealed class InputViewEffect {
    object MoveReturn : InputViewEffect()
    object MoveEdit : InputViewEffect()
    object MoveCalendar : InputViewEffect()
    object MoveOnBoarding : InputViewEffect()
    object MovePinPassword : InputViewEffect()
}