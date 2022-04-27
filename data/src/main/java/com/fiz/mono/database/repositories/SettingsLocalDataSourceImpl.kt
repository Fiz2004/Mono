package com.fiz.mono.database.repositories

import android.content.SharedPreferences
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSourceImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    SettingsLocalDataSource {
    override fun loadFirstTime() = sharedPreferences.getBoolean(FIRST_TIME, true)

    override fun saveFirstTime(firstTime: Boolean) =
        sharedPreferences.edit().putBoolean(FIRST_TIME, firstTime).apply()


    override fun loadPin(): String = sharedPreferences.getString(PIN, "") ?: ""

    override fun savePin(pin: String) =
        sharedPreferences.edit().putString(PIN, pin).apply()

    override fun loadConfirmPin(): Boolean = sharedPreferences.getBoolean(CONFIRM_PIN, false)

    override fun saveConfirmPin(confirmPin: Boolean) =
        sharedPreferences.edit().putBoolean(CONFIRM_PIN, confirmPin).apply()

    override fun loadCurrency(): String = sharedPreferences.getString(CURRENCY, "$") ?: "$"

    override fun saveCurrency(currency: String) =
        sharedPreferences.edit().putString(CURRENCY, currency).apply()

    override fun loadThemeLight(): Boolean = sharedPreferences.getBoolean(THEME_LIGHT, false)

    override fun saveThemeLight(themeLight: Boolean) =
        sharedPreferences.edit().putBoolean(THEME_LIGHT, themeLight).apply()

    companion object {
        const val FIRST_TIME = "FIRST_TIME"
        const val PIN = "PIN"
        const val CONFIRM_PIN = "CONFIRM_PIN"
        const val CURRENCY = "CURRENCY"
        const val THEME_LIGHT = "THEME_LIGHT"
    }
}