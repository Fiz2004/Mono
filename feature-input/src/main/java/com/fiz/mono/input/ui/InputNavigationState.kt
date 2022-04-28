package com.fiz.mono.input.ui

data class InputNavigationState(
    val isMoveReturn: Boolean = false,
    val isMoveEdit: Boolean = false,
    val isMoveCalendar: Boolean = false,
    val isMoveOnBoarding: Boolean = false,
    val isMovePinPassword: Boolean = false
)