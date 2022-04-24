package com.fiz.mono.feature_pin_password.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PINPasswordViewModel @Inject constructor() : ViewModel() {

    private var _statePIN = MutableLiveData(StatePin.LOGIN_FINISH)
    val statePIN: LiveData<StatePin> = _statePIN

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    private val _pin: MutableLiveData<MutableList<Int?>> =
        MutableLiveData(mutableListOf(null, null, null, null))
    val pin: LiveData<MutableList<Int?>> = _pin

    fun initState(fromCome: String, pin: String) {
        _statePIN.value = if (fromCome == PINPasswordFragment.START) {
            if (pin.isBlank())
                StatePin.LOGIN_FINISH
            else
                StatePin.LOGIN
        } else {
            if (pin.isBlank())
                StatePin.CREATE
            else
                StatePin.REMOVE
        }
    }

    fun clickEditButton() {
        _statePIN.value = StatePin.EDIT
    }

    fun exitError() {
        if (statePIN.value == StatePin.REMOVE_CONFIRM_ERROR) {
            _statePIN.value = StatePin.REMOVE_CONFIRM
        }
        if (statePIN.value == StatePin.EDIT_ERROR) {
            _statePIN.value = StatePin.EDIT
        }
        if (statePIN.value == StatePin.LOGIN_ERROR) {
            _statePIN.value = StatePin.LOGIN
        }
    }

    fun updateState(oldPin: String) {
        if (statePIN.value == StatePin.CREATE) {
            _statePIN.value =
                StatePin.CREATE_FINISH
            return
        }

        if (statePIN.value == StatePin.REMOVE) {
            _statePIN.value = StatePin.REMOVE_CONFIRM
            return
        }

        if (statePIN.value == StatePin.REMOVE_CONFIRM) {
            _statePIN.value =
                if (oldPin == getPIN())
                    StatePin.REMOVE_CONFIRM_FINISH
                else
                    StatePin.REMOVE_CONFIRM_ERROR
            return
        }

        if (statePIN.value == StatePin.EDIT) {
            _statePIN.value =
                if (oldPin == getPIN())
                    StatePin.CREATE
                else
                    StatePin.EDIT_ERROR
            return
        }

        if (statePIN.value == StatePin.LOGIN) {
            if (oldPin == getPIN()) {
                _statePIN.value = StatePin.LOGIN_FINISH
            } else {
                _statePIN.value = StatePin.LOGIN_ERROR
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