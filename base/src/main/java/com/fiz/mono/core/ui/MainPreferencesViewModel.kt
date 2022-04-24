package com.fiz.mono.core.ui

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainPreferencesViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var _firstTime = MutableLiveData(false)
    val isFirstTime: LiveData<Boolean> = _firstTime

    private var _currency = MutableLiveData("$")
    val currency: LiveData<String> = _currency

    private var _pin = MutableLiveData("")
    val pin: LiveData<String> = _pin

    private var _themeLight = MutableLiveData(true)
    val themeLight: LiveData<Boolean> = _themeLight

    private var _isConfirmPIN = MutableLiveData(false)
    val isConfirmPIN: LiveData<Boolean> = _isConfirmPIN

    init {
        _firstTime.value = sharedPreferences.getBoolean("firstTime", true)
        setCurrency(sharedPreferences.getString("currency", "$") ?: "$")
        _pin.value = sharedPreferences.getString("PIN", "") ?: ""
        _themeLight.value = sharedPreferences.getBoolean("themeLight", true)
        if (pin.value?.isBlank() == true)
            _isConfirmPIN.value = true
    }

    fun setCurrency(loadCurrency: String) {
        _currency.value = loadCurrency
        sharedPreferences
            .edit()
            .putString("currency", loadCurrency)
            .apply()
    }

    fun changeFirstTime() {
        _firstTime.value = false
        sharedPreferences.edit()
            .putBoolean("firstTime", isFirstTime.value ?: false)
            .apply()
    }

    fun deletePin() {
        _pin.value = ""
        sharedPreferences
            .edit()
            .putString("PIN", pin.value)
            .apply()
    }

    fun setPin(pin: String) {
        _pin.value = pin
        sharedPreferences
            .edit()
            .putString("PIN", pin)
            .apply()
    }

    fun setThemeLight(themeLight: Boolean) {
        _themeLight.value = themeLight
        sharedPreferences
            .edit()
            .putBoolean("themeLight", themeLight)
            .apply()
    }

    fun confirmPin() {
        _isConfirmPIN.value = true
    }
}