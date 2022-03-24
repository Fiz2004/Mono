package com.fiz.mono.ui.pin_password

import androidx.lifecycle.ViewModel

class PINPasswordViewModel : ViewModel() {
    var statePIN = ""

    fun getState(fromCome: String, PIN: String) {
        if (fromCome == PINPasswordFragment.START && PIN.isNotBlank()) {
            statePIN = PINPasswordFragment.STATE_LOGIN
            return
        }

        if (fromCome == PINPasswordFragment.SETTINGS && PIN.isBlank()) {
            statePIN = PINPasswordFragment.STATE_CREATE
            return
        }

        if (fromCome == PINPasswordFragment.SETTINGS && PIN.isNotBlank()) {
            statePIN = PINPasswordFragment.STATE_REMOVE
            return
        }

        statePIN = PINPasswordFragment.STATE_ERROR
    }
}