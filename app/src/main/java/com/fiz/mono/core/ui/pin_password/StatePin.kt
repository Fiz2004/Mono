package com.fiz.mono.core.ui.pin_password

enum class StatePin {
    LOGIN,
    LOGIN_ERROR,
    LOGIN_FINISH,

    CREATE,
    CREATE_FINISH,

    REMOVE,
    REMOVE_CONFIRM,
    REMOVE_CONFIRM_ERROR,
    REMOVE_CONFIRM_FINISH,

    EDIT,
    EDIT_ERROR,
    EDIT_FINISH
}

fun StatePin.isError(): Boolean {
    return this == StatePin.LOGIN_ERROR || this == StatePin.REMOVE_CONFIRM_ERROR || this == StatePin.EDIT_ERROR
}

fun StatePin.isNotLogin(): Boolean {
    return !(this == StatePin.LOGIN || this == StatePin.LOGIN_ERROR || this == StatePin.LOGIN_FINISH)
}