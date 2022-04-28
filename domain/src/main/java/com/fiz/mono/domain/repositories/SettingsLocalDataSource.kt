package com.fiz.mono.domain.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface SettingsLocalDataSource {
    val firstTimeFlow:MutableStateFlow<Boolean>

    fun loadPin(): String

    fun savePin(pin: String)

    fun loadConfirmPin(): Flow<Boolean>

    fun saveConfirmPin(confirmPin: Boolean)

    suspend fun loadFirstTime()

    suspend fun saveFirstTime(firstTime: Boolean)

    fun loadCurrency(): String

    fun saveCurrency(currency: String)

    fun loadThemeLight(): Boolean

    fun saveThemeLight(themeLight: Boolean)
}