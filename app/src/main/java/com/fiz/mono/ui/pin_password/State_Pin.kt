package com.fiz.mono.ui.pin_password

enum class State_Pin {
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

fun State_Pin.isError(): Boolean {
    return this == State_Pin.LOGIN_ERROR || this == State_Pin.REMOVE_CONFIRM_ERROR || this == State_Pin.EDIT_ERROR
}

fun State_Pin.isNotLogin(): Boolean {
    return !(this == State_Pin.LOGIN || this == State_Pin.LOGIN_ERROR || this == State_Pin.LOGIN_FINISH)
}