package com.fiz.mono.domain.repositories

interface SettingsLocalDataSource {
    fun loadPin(): String

    fun savePin(pin: String)

    fun loadConfirmPin(): Boolean

    fun saveConfirmPin(confirmPin: Boolean)

    fun loadFirstTime(): Boolean

    fun saveFirstTime(firstTime: Boolean)

    fun loadCurrency(): String

    fun saveCurrency(currency: String)

    fun loadThemeLight(): Boolean

    fun saveThemeLight(themeLight: Boolean)
}