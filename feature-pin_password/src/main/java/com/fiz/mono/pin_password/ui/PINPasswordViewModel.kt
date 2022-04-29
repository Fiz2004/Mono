package com.fiz.mono.pin_password.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PINPasswordViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    var uiState = MutableStateFlow(PINPasswordUiState()); private set

    var navigationState = MutableStateFlow(PINPasswordNavigationState()); private set


    init {
        settingsRepository.pin.load()
            .onEach { pin ->
                uiState.value = uiState.value
                    .copy(pinValue = pin)
            }.launchIn(viewModelScope)
    }

    fun start(fromCome: String) {
        val isConfirmPIN = uiState.value.pinValue.isBlank()

        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(isConfirmPIN)
        }

        val statePIN = if (fromCome == PINPasswordFragment.START) {
            if (uiState.value.pinValue.isBlank())
                StatePin.LOGIN_FINISH
            else
                StatePin.LOGIN
        } else {
            if (uiState.value.pinValue.isBlank())
                StatePin.CREATE
            else
                StatePin.REMOVE
        }

        uiState.value = uiState.value
            .copy(
                statePIN = statePIN,
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
        val oldPin = uiState.value.pinValue

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
        viewModelScope.launch {
            settingsRepository.pin.save("")
        }
        navigationState.value = navigationState.value
            .copy(isReturn = true)
    }

    fun setPin(pin: String) {
        navigationState.value = navigationState.value
            .copy(isReturn = true)

        viewModelScope.launch {
            settingsRepository.pin.save(pin)
            settingsRepository.needConfirmPin.save(true)
        }
    }

    fun loginFinish() {
        navigationState.value = navigationState.value
            .copy(isReturn = true)

        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(true)
        }
    }

    fun returned() {
        navigationState.value = navigationState.value
            .copy(isReturn = false)
    }
}