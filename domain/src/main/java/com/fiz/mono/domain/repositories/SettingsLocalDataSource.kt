package com.fiz.mono.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    fun loadPin(): String

    fun savePin(pin: String)

    fun loadConfirmPin(): Flow<Boolean>

    fun saveConfirmPin(confirmPin: Boolean)

    fun loadFirstTime(): Flow<Boolean>

    fun saveFirstTime(firstTime: Boolean)

    fun loadCurrency(): String

    fun saveCurrency(currency: String)

    fun loadThemeLight(): Boolean

    fun saveThemeLight(themeLight: Boolean)
}