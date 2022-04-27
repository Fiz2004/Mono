package com.fiz.mono.pin_password.ui

data class PINPasswordUiState(
    val statePIN: StatePin = StatePin.LOGIN_FINISH,
    val pin: MutableList<Int?> =
        mutableListOf(null, null, null, null),
    val isConfirmPIN: Boolean = false
)