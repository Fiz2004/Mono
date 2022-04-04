package com.fiz.mono.ui.pin_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PINPasswordViewModel : ViewModel() {

    private var _statePIN = MutableLiveData(State_Pin.LOGIN_FINISH)
    val statePIN: LiveData<State_Pin> = _statePIN

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    private val _pin: MutableLiveData<MutableList<Int?>> =
        MutableLiveData(mutableListOf(null, null, null, null))
    val pin: LiveData<MutableList<Int?>> = _pin

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

    fun clickEditButton() {
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

    fun updateState(oldPin: String) {
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
                if (oldPin == getPIN())
                    State_Pin.REMOVE_CONFIRM_FINISH
                else
                    State_Pin.REMOVE_CONFIRM_ERROR
            return
        }

        if (statePIN.value == State_Pin.EDIT) {
            _statePIN.value =
                if (oldPin == getPIN())
                    State_Pin.CREATE
                else
                    State_Pin.EDIT_ERROR
            return
        }

        if (statePIN.value == State_Pin.LOGIN) {
            if (oldPin == getPIN()) {
                _statePIN.value = State_Pin.LOGIN_FINISH
            } else {
                _statePIN.value = State_Pin.LOGIN_ERROR
            }
            return
        }
    }

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun setText(number: Int, text: CharSequence?) {
        _pin.value?.set(
            number, try {
                text.toString().toInt()
            } catch (e: Exception) {
                null
            }
        )
        _pin.value = pin.value
    }

    fun getPIN() =
        _pin.value?.get(0).toString() + _pin.value?.get(1).toString() +
                _pin.value?.get(2).toString() + _pin.value?.get(3).toString()

    fun isNextPINPasswordButtonEnabled(): Boolean {
        return _pin.value?.get(0) != null &&
                _pin.value?.get(1) != null &&
                _pin.value?.get(2) != null &&
                _pin.value?.get(3) != null
    }
}