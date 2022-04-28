package com.fiz.mono.pin_password.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PINPasswordViewModel @Inject constructor(private val settingsLocalDataSource: SettingsLocalDataSource) :
    ViewModel() {

    var uiState = MutableStateFlow(PINPasswordUiState()); private set

    var navigationState = MutableStateFlow(PINPasswordNavigationState()); private set

    fun start(fromCome: String) {
        val pin = settingsLocalDataSource.loadPin()
        val isConfirmPIN = pin.isBlank()

        viewModelScope.launch {
            settingsLocalDataSource.saveCurrentConfirmPin(isConfirmPIN)
        }

        val statePIN = if (fromCome == PINPasswordFragment.START) {
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

        uiState.value = uiState.value
            .copy(
                statePIN = statePIN,
                isConfirmPIN = isConfirmPIN
            )
    }

    fun clickEditButton() {
        uiState.value = uiState.value
            .copy(
                statePIN = StatePin.EDIT
            )
    }

    fun exitError() {
        val statePin: StatePin = when {
            (uiState.value.statePIN == StatePin.REMOVE_CONFIRM_ERROR) -> {
                StatePin.REMOVE_CONFIRM
            }
            (uiState.value.statePIN == StatePin.EDIT_ERROR) -> {
                StatePin.EDIT
            }
            (uiState.value.statePIN == StatePin.LOGIN_ERROR) -> {
                StatePin.LOGIN
            }
            else -> {
                return
            }
        }

        uiState.value = uiState.value
            .copy(
                statePIN = statePin
            )
    }

    fun updateState() {
        val oldPin = settingsLocalDataSource.loadPin()

        val statePin: StatePin = when {
            (uiState.value.statePIN == StatePin.CREATE) -> {

                StatePin.CREATE_FINISH
            }
            (uiState.value.statePIN == StatePin.REMOVE) -> {
                StatePin.REMOVE_CONFIRM
            }
            (uiState.value.statePIN == StatePin.REMOVE_CONFIRM) -> {
                if (oldPin == getPIN())
                    StatePin.REMOVE_CONFIRM_FINISH
                else
                    StatePin.REMOVE_CONFIRM_ERROR
            }

            (uiState.value.statePIN == StatePin.EDIT) -> {

                if (oldPin == getPIN())
                    StatePin.CREATE
                else
                    StatePin.EDIT_ERROR
            }

            (uiState.value.statePIN == StatePin.LOGIN) -> {
                if (oldPin == getPIN()) {
                    StatePin.LOGIN_FINISH
                } else {
                    StatePin.LOGIN_ERROR
                }
            }
            else -> return
        }

        uiState.value = uiState.value
            .copy(
                statePIN = statePin
            )
    }

    fun clickBackButton() {
        navigationState.value = navigationState.value
            .copy(
                isReturn = true
            )
    }

    fun setText(number: Int, text: CharSequence?) {
        val pin = uiState.value.pin.toMutableList()

        pin[number] = try {
            text.toString().toInt()
        } catch (e: Exception) {
            null
        }

        uiState.value = uiState.value
            .copy(
                pin = pin
            )
    }

    fun getPIN() =
        uiState.value.pin[0].toString() +
                uiState.value.pin[1].toString() +
                uiState.value.pin[2].toString() +
                uiState.value.pin[3].toString()

    fun isNextPINPasswordButtonEnabled(): Boolean {
        return uiState.value.pin[0] != null &&
                uiState.value.pin[1] != null &&
                uiState.value.pin[2] != null &&
                uiState.value.pin[3] != null
    }

    fun isCanPopBackStack(): Boolean {
        return uiState.value.statePIN != StatePin.LOGIN
    }

    fun getPin(): List<Int?> {
        return uiState.value.pin
    }

    fun deletePin() {
        settingsLocalDataSource.savePin("")
    }

    fun setPin(pin: String) {
        settingsLocalDataSource.savePin(pin)
        uiState.value = uiState.value
            .copy(
                isConfirmPIN = true
            )

        viewModelScope.launch {
            settingsLocalDataSource.saveNeedConfirmPin(true)
        }
    }
}