package com.fiz.mono.ui.pin_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PINPasswordViewModel : ViewModel() {

    private var _statePIN = MutableLiveData(State_Pin.LOGIN_FINISH)
    val statePIN: LiveData<State_Pin> = _statePIN

    fun initState(fromCome: String, pin: String) {
        _statePIN.value = if (fromCome == PINPasswordFragment.START) {
            if (pin.isBlank())
                State_Pin.LOGIN_FINISH
            else
                State_Pin.LOGIN
        } else {
            if (pin.isBlank())
                State_Pin.CREATE
            else
                State_Pin.REMOVE
        }
    }

    fun changeStateOnEdit() {
        _statePIN.value = State_Pin.EDIT
    }

    fun exitError() {
        if (statePIN.value == State_Pin.REMOVE_CONFIRM_ERROR) {
            _statePIN.value = State_Pin.REMOVE_CONFIRM
        }
        if (statePIN.value == State_Pin.EDIT_ERROR) {
            _statePIN.value = State_Pin.EDIT
        }
        if (statePIN.value == State_Pin.LOGIN_ERROR) {
            _statePIN.value = State_Pin.LOGIN
        }
    }

    fun updateState(oldPin: String, currentPin: String) {
        if (statePIN.value == State_Pin.CREATE) {
            _statePIN.value =
                State_Pin.CREATE_FINISH
            return
        }

        if (statePIN.value == State_Pin.REMOVE) {
            _statePIN.value = State_Pin.REMOVE_CONFIRM
            return
        }

        if (statePIN.value == State_Pin.REMOVE_CONFIRM) {
            _statePIN.value =
                if (oldPin == currentPin)
                    State_Pin.REMOVE_CONFIRM_FINISH
                else
                    State_Pin.REMOVE_CONFIRM_ERROR
            return
        }

        if (statePIN.value == State_Pin.EDIT) {
            _statePIN.value =
                if (oldPin == currentPin)
                    State_Pin.CREATE
                else
                    State_Pin.EDIT_ERROR
            return
        }

        if (statePIN.value == State_Pin.LOGIN) {
            if (oldPin == currentPin) {
                _statePIN.value = State_Pin.LOGIN_FINISH
            } else {
                _statePIN.value = State_Pin.LOGIN_ERROR
            }
            return
        }
    }
}