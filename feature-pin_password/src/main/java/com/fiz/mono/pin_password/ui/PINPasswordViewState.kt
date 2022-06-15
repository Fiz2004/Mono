package com.fiz.mono.pin_password.ui

data class PINPasswordViewState(
    val statePIN: StatePin = StatePin.LOGIN_FINISH,
    val pin: MutableList<Int?> =
        mutableListOf(null, null, null, null),

    val pinValue: String = "",
)