package com.fiz.mono.pin_password.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PINPasswordViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    var viewState = MutableStateFlow(PINPasswordViewState())
        private set

    var viewEffects = MutableSharedFlow<PINPasswordViewEffect>()
        private set


    init {
        settingsRepository.pin.load()
            .onEach { pin ->
                viewState.value = viewState.value
                    .copy(pinValue = pin)
            }.launchIn(viewModelScope)
    }

    fun start(fromCome: String) {
        val isConfirmPIN = viewState.value.pinValue.isBlank()

        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(isConfirmPIN)
        }

        val statePIN = if (fromCome == PINPasswordFragment.START) {
            if (viewState.value.pinValue.isBlank())
                StatePin.LOGIN_FINISH
            else
                StatePin.LOGIN
        } else {
            if (viewState.value.pinValue.isBlank())
                StatePin.CREATE
            else
                StatePin.REMOVE
        }

        viewState.value = viewState.value
            .copy(
                statePIN = statePIN,
            )
    }

    fun clickEditButton() {
        viewState.value = viewState.value
            .copy(
                statePIN = StatePin.EDIT
            )
    }

    fun exitError() {
        val statePin: StatePin = when {
            (viewState.value.statePIN == StatePin.REMOVE_CONFIRM_ERROR) -> {
                StatePin.REMOVE_CONFIRM
            }
            (viewState.value.statePIN == StatePin.EDIT_ERROR) -> {
                StatePin.EDIT
            }
            (viewState.value.statePIN == StatePin.LOGIN_ERROR) -> {
                StatePin.LOGIN
            }
            else -> {
                return
            }
        }

        viewState.value = viewState.value
            .copy(
                statePIN = statePin
            )
    }

    fun updateState() {
        val oldPin = viewState.value.pinValue

        val statePin: StatePin = when {
            (viewState.value.statePIN == StatePin.CREATE) -> {

                StatePin.CREATE_FINISH
            }
            (viewState.value.statePIN == StatePin.REMOVE) -> {
                StatePin.REMOVE_CONFIRM
            }
            (viewState.value.statePIN == StatePin.REMOVE_CONFIRM) -> {
                if (oldPin == getPIN())
                    StatePin.REMOVE_CONFIRM_FINISH
                else
                    StatePin.REMOVE_CONFIRM_ERROR
            }

            (viewState.value.statePIN == StatePin.EDIT) -> {

                if (oldPin == getPIN())
                    StatePin.CREATE
                else
                    StatePin.EDIT_ERROR
            }

            (viewState.value.statePIN == StatePin.LOGIN) -> {
                if (oldPin == getPIN()) {
                    StatePin.LOGIN_FINISH
                } else {
                    StatePin.LOGIN_ERROR
                }
            }
            else -> return
        }

        viewState.value = viewState.value
            .copy(
                statePIN = statePin
            )
    }

    fun clickBackButton() {
        viewModelScope.launch {
            viewEffects.emit(PINPasswordViewEffect.MoveReturn)
        }
    }

    fun setText(number: Int, text: CharSequence?) {
        val pin = viewState.value.pin.toMutableList()

        pin[number] = try {
            text.toString().toInt()
        } catch (e: Exception) {
            null
        }

        viewState.value = viewState.value
            .copy(
                pin = pin
            )
    }

    fun getPIN() =
        viewState.value.pin[0].toString() +
                viewState.value.pin[1].toString() +
                viewState.value.pin[2].toString() +
                viewState.value.pin[3].toString()

    fun isNextPINPasswordButtonEnabled(): Boolean {
        return viewState.value.pin[0] != null &&
                viewState.value.pin[1] != null &&
                viewState.value.pin[2] != null &&
                viewState.value.pin[3] != null
    }

    fun isCanPopBackStack(): Boolean {
        return viewState.value.statePIN != StatePin.LOGIN
    }

    fun getPin(): List<Int?> {
        return viewState.value.pin
    }

    fun deletePin() {
        viewModelScope.launch {
            settingsRepository.pin.save("")
            viewEffects.emit(PINPasswordViewEffect.MoveReturn)
        }
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            settingsRepository.pin.save(pin)
            settingsRepository.needConfirmPin.save(true)
            viewEffects.emit(PINPasswordViewEffect.MoveReturn)
        }
    }

    fun loginFinish() {
        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(true)
            viewEffects.emit(PINPasswordViewEffect.MoveReturn)
        }
    }
}