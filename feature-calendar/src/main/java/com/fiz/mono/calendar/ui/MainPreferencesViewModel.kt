package com.fiz.mono.calendar.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainPreferencesViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {
    var firstTime = MutableStateFlow(true); private set

    var isConfirmPIN = MutableStateFlow(false); private set

    var pin = MutableLiveData(""); private set

    var currency = MutableLiveData("$"); private set

    var themeLight = MutableLiveData(true); private set

    init {
        currency.value = settingsLocalDataSource.loadCurrency()
        pin.value = settingsLocalDataSource.loadPin()
        themeLight.value = settingsLocalDataSource.loadThemeLight()
    }

    init {
        viewModelScope.launch {
            settingsLocalDataSource.firstTimeFlow.collect {
                firstTime.value = it
            }
        }

        viewModelScope.launch {
            settingsLocalDataSource.loadConfirmPin().collect {
                isConfirmPIN.value = if (pin.value?.isBlank() == true)
                    true
                else
                    it
            }
        }
    }

    fun start() {
        currency.value = settingsLocalDataSource.loadCurrency()
        pin.value = settingsLocalDataSource.loadPin()
        themeLight.value = settingsLocalDataSource.loadThemeLight()
    }

    fun setCurrency(currency: String) {
        this.currency.value = currency
        settingsLocalDataSource.saveCurrency(currency)
    }

    fun setThemeLight(themeLight: Boolean) {
        this.themeLight.value = themeLight
        settingsLocalDataSource.saveThemeLight(themeLight)
    }
}