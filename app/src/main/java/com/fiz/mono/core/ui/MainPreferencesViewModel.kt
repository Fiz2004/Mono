package com.fiz.mono.core.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainPreferencesViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {
    var firstTime = MutableLiveData(false); private set

    var currency = MutableLiveData("$"); private set

    var pin = MutableLiveData(""); private set

    var themeLight = MutableLiveData(true); private set

    var isConfirmPIN = MutableLiveData(false); private set

    init {
        firstTime.value = settingsLocalDataSource.loadFirstTime()
        currency.value = settingsLocalDataSource.loadCurrency()
        pin.value = settingsLocalDataSource.loadPin()
        themeLight.value = settingsLocalDataSource.loadThemeLight()
        isConfirmPIN.value = pin.value?.isBlank() == true
    }

    fun start() {
        firstTime.value = settingsLocalDataSource.loadFirstTime()
        currency.value = settingsLocalDataSource.loadCurrency()
        pin.value = settingsLocalDataSource.loadPin()
        themeLight.value = settingsLocalDataSource.loadThemeLight()
        isConfirmPIN.value = getIsConfirmPIN()
    }

    fun setCurrency(currency: String) {
        this.currency.value = currency
        settingsLocalDataSource.saveCurrency(currency)
    }

    fun setThemeLight(themeLight: Boolean) {
        this.themeLight.value = themeLight
        settingsLocalDataSource.saveThemeLight(themeLight)
    }

    fun getIsConfirmPIN(): Boolean {
        return if (pin.value?.isBlank() == true)
            true
        else
            settingsLocalDataSource.loadConfirmPin()
    }

}