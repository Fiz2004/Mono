package com.fiz.mono.domain.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate

data class Settings(
    val firstTime: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val isNeedConfirmPIN: Boolean = false,
    val isCurrentConfirmPIN: Boolean = false,
    val currency: String = "$"
)

interface SettingsLocalDataSource {
    val stateFlow: MutableStateFlow<Settings>

    fun loadPin(): String

    fun savePin(pin: String)

    suspend fun loadNeedConfirmPin()

    suspend fun saveNeedConfirmPin(confirmPin: Boolean)

    suspend fun loadCurrentConfirmPin()

    suspend fun saveCurrentConfirmPin(confirmPin: Boolean)

    suspend fun loadFirstTime()

    suspend fun saveFirstTime(firstTime: Boolean)

    suspend fun loadCurrency()

    suspend fun saveCurrency(currency: String)

    fun loadThemeLight(): Boolean

    fun saveThemeLight(themeLight: Boolean)

    suspend fun loadDate()

    suspend fun saveDate(date: LocalDate)
}